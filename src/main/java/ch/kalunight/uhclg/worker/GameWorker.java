package ch.kalunight.uhclg.worker;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import com.google.common.base.Stopwatch;

import ch.kalunight.uhclg.GameData;
import ch.kalunight.uhclg.ZoePluginMaster;
import ch.kalunight.uhclg.mincraft.commands.LgListe;
import ch.kalunight.uhclg.mincraft.commands.LgVoir;
import ch.kalunight.uhclg.model.GameStatus;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.RoleClan;
import ch.kalunight.uhclg.model.TimeStatus;
import ch.kalunight.uhclg.model.VoiceRequest;
import ch.kalunight.uhclg.model.VoiceStatus;
import ch.kalunight.uhclg.model.role.RoleImpl;

public class GameWorker implements Runnable {

  private static final int DAY_DURATION = 24000;
  private static final Duration DAY1_DURATION = Duration.ofMinutes(1);
  private static final Duration DAY2_DURATION = Duration.ofMinutes(1);
  private static final Duration PVP_START_DURATION = Duration.ofMinutes(30);

  private static final int START_OF_DAY = 0;

  private static final int START_OF_NIGHT = 12000;

  private static final int ROLE_START_DAY_NUMBER = 2;

  private static TimeStatus timeStatus;

  private static long secondsTime;

  private static long actualTime;

  private static long actualDayNumber;

  private static Stopwatch chrono;

  private static boolean pvpActivate;

  private static World world;

  @Override
  public void run() {
    if(GameData.getGameStatus().equals(GameStatus.IN_GAME)) {
      defineWorldTime();

      checkPVP();

      if(actualDayNumber >= ROLE_START_DAY_NUMBER) {
        givePlayerEffect();
      }
    }
  }

  private void givePlayerEffect() {
    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.isAlive() && player.isConnected()) {
        player.getRole().givePotionEffect(player, timeStatus);
        RoleImpl.giveEffectInfected(player, timeStatus);
      }
    }
  }

  private void checkPVP() {
    if(!pvpActivate && chrono.elapsed(TimeUnit.MINUTES) > PVP_START_DURATION.toMinutes()) {
      for(World worldToChange : ZoePluginMaster.getMinecraftServer().getWorlds()) {
        worldToChange.setGameRule(GameRule.NATURAL_REGENERATION, true); //TODO : Disable natural regeneration ?
        worldToChange.setPVP(true);
      }

      pvpActivate = true;
    }
  }

  private void defineWorldTime() {
    setActualTime(actualTime + secondsTime);

    if(actualTime > DAY_DURATION) {
      setSecondsTime(DAY_DURATION / DAY2_DURATION.getSeconds());
      setActualTime(0);
      setTimeStatus(TimeStatus.DAY);
      actualDayNumber++;
      world.setTime(0);

      if(actualDayNumber == ROLE_START_DAY_NUMBER) {
        ZoePluginMaster.getMinecraftServer().broadcastMessage("Le jour 2 commence ! "
            + "Vos rôles sont dévoilés maintenant ! Le PVP sera activé à " + PVP_START_DURATION.getSeconds() / 60 + " minutes !");
        sayRoleToPlayer(); //Les rôles sont disponible a partir du jour 2
        giveStuffOfRole();
      }

      sayMessageOfDay();

      WorldBorder worldBorder = world.getWorldBorder();
      if(worldBorder.getSize() > 750) {
        double worldBorderSize = worldBorder.getSize() - 500;
        worldBorder.setSize(worldBorderSize, 180);
        sayWorldBorderMove(worldBorderSize > 750);
      }else {
        double newSize = worldBorder.getSize() / 2;
        worldBorder.setSize(newSize, 300);
        ZoePluginMaster.getMinecraftServer().broadcastMessage("La carte se réduit de " + (int) newSize + " blocs ! ");
      }

    } else {
      if(actualTime > START_OF_NIGHT) {
        setTimeStatus(TimeStatus.NIGHT);
      }else {
        setTimeStatus(TimeStatus.DAY);
      }
      world.setTime(actualTime);
    }
  }

  private void sayWorldBorderMove(boolean minSizeHit) {
    if(!minSizeHit) {
      ZoePluginMaster.getMinecraftServer().broadcastMessage("La carte se réduit de 500 blocs !");
    }else {
      ZoePluginMaster.getMinecraftServer()
      .broadcastMessage("La carte se réduit de 500 blocs ! Elle rétraicira plus doucement à partir de maintenant.");
      world.getWorldBorder().setWarningDistance(0);
    }

  }

  private void sayMessageOfDay() {

    PlayerData voyante = null;

    for(PlayerData player : GameData.getPlayersInGame()) {
      if(player.getRole().getRoleEnum().equals(Role.VOYANTE)) {
        voyante = player;
      }
    }

    if(voyante != null && voyante.isConnected() && voyante.isAlive()) {
      voyante.getAccount().getPlayer().sendMessage("Vous pouvez utiliser votre pouvoir de voyante maintenant avec la commande \"lgvoir nomDuJoueur\".");
    }

    LgVoir.setRoleChecked(false);
  }

  private void giveStuffOfRole() {
    for(PlayerData playerData : GameData.getPlayersInGame()) {
      playerData.getRole().giveRoleEffectAndItem(playerData);
    }
  }

  private void sayRoleToPlayer() {
    VocalSystemWorker.setVoiceStatus(VoiceStatus.SOLO_ANNONCEMENT);
    for(PlayerData player : GameData.getPlayersInGame()) {
      RoleImpl role = player.getRole();

      player.getAccount().getPlayer().sendMessage("Votre rôle : " + role.getName());

      VocalSystemWorker.getVoiceRequests().add(new VoiceRequest(GameData.getLobby().getGuild().getIdLong(),
          player.getAccount().getDiscordId(), role.getVoiceFile().getAbsolutePath(), true));

      player.getAccount().getPlayer().sendMessage("Description : " + role.getDescription());
      if(player.getRole().getRoleEnum().getClan().equals(RoleClan.WOLFS) 
          && !player.getRole().getRoleEnum().equals(Role.LOUP_GAROU_AMNESIQUE)) {
        player.getAccount().getPlayer().sendMessage(LgListe.getListWolfs());
      }
    }
  }

  public static void setupGameWorker() {
    world.setTime(START_OF_DAY);
    setTimeStatus(TimeStatus.DAY);
    world.getWorldBorder().setWarningDistance(500);

    secondsTime = DAY_DURATION / DAY1_DURATION.getSeconds();
    actualTime = 0;

    actualDayNumber = 1;
    pvpActivate = false;

    world.setTime(actualTime);
  }

  public static String getChronoString() {
    long seconds = chrono.elapsed(TimeUnit.SECONDS);
    long absSeconds = Math.abs(seconds);
    return String.format(
        "%02d:%02d",
        (absSeconds % 3600) / 60,
        absSeconds % 60);
  }

  public static Stopwatch getGameStartTime() {
    return chrono;
  }

  public static void setGameStartTime(Stopwatch gameStartTime) {
    GameWorker.chrono = gameStartTime;
  }

  public static World getWorld() {
    return world;
  }

  public static void setWorld(World world) {
    GameWorker.world = world;
  }

  public static TimeStatus getTimeStatus() {
    return timeStatus;
  }

  public static void setTimeStatus(TimeStatus timeStatus) {
    GameWorker.timeStatus = timeStatus;
  }

  public static long getSecondsTime() {
    return secondsTime;
  }

  public static void setSecondsTime(long secondsTime) {
    GameWorker.secondsTime = secondsTime;
  }

  public static long getActualTime() {
    return actualTime;
  }

  public static long getActualDayNumber() {
    return actualDayNumber;
  }

  public static void setActualTime(long actualTime) {
    GameWorker.actualTime = actualTime;
  }

}
