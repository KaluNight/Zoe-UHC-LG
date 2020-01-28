package ch.kalunight.uhclg.model.role;

import java.io.File;
import ch.kalunight.uhclg.model.PlayerData;
import ch.kalunight.uhclg.model.Role;
import ch.kalunight.uhclg.model.TimeStatus;

public class EnfantSauvage implements RoleImpl {

  private static final File annonceVoiceFile = new File(Role.SOUNDS_FOLDER + "enfant_sauvage.wav");
  
  private PlayerData model;
  
  @Override
  public Role getRole() {
    return Role.ENFANT_SAUVAGE;
  }

  @Override
  public String getName() {
    return "Enfant Sauvage";
  }

  @Override
  public String getDescription() {
    return "Enfant sauvage : Il choisit un modèle via une commande "
        + "(/lgmodele pseudo du modèle) quand cette personne meurt, "
        + "il devient loup et reçoit leur bonus et 2mn après la liste.";
  }

  @Override
  public File getVoiceFile() {
    return annonceVoiceFile;
  }

  @Override
  public String getVoicePath() {
    return annonceVoiceFile.getAbsolutePath();
  }

  @Override
  public void giveRoleEffectAndItem(PlayerData player) {
    //nothing
  }

  @Override
  public void givePotionEffect(PlayerData player, TimeStatus time) {
    //TODO Implement model effect
  }

  public PlayerData getModel() {
    return model;
  }

  public void setModel(PlayerData model) {
    this.model = model;
  }
}
