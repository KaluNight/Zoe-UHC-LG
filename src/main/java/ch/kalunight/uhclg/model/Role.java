package ch.kalunight.uhclg.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.kalunight.uhclg.GameData;

public enum Role {
  /*ANGE(RoleClan.VILLAGE, "Ange"),*/
  VOYANTE(RoleClan.VILLAGE, "Voyante", new File(Role.SOUNDS_FOLDER + "voyante.wav")),
  CUPIDON(RoleClan.VILLAGE, "Cupidon", new File(Role.SOUNDS_FOLDER + "cupidon.wav")),
  PETITE_FILLE(RoleClan.VILLAGE, "Petite Fille", new File(Role.SOUNDS_FOLDER + "petite_fille.wav")),
  SORCIERE(RoleClan.VILLAGE, "Sorcière", new File(Role.SOUNDS_FOLDER + "sorciere.wav")),
  ANCIEN(RoleClan.VILLAGE, "Ancien", new File(Role.SOUNDS_FOLDER + "ancien.wav")),
  RENARD(RoleClan.VILLAGE, "Renard", new File(Role.SOUNDS_FOLDER + "renard.wav")),
  VILLAGEOIS(RoleClan.VILLAGE, "Villageois", new File(Role.SOUNDS_FOLDER + "villageois.wav")),
  ASSASSIN(RoleClan.SPECIAL, "Assassin", new File(Role.SOUNDS_FOLDER + "assassin.wav")),
  LOUP_GAROU(RoleClan.WOLFS, "Loup Garou", new File(Role.SOUNDS_FOLDER + "loup_garou.wav")),
  ENFANT_SAUVAGE(RoleClan.WOLFS, "Enfant Sauvage", new File(Role.SOUNDS_FOLDER + "enfant_sauvage.wav")),
  INFECT_PERE_DES_LOUPS(RoleClan.WOLFS, "Infect Père des Loups Garou", new File(Role.SOUNDS_FOLDER + "infect_loup.wav")),
  LOUP_GAROU_BLANC(RoleClan.WOLFS, "Loup Garou Blanc", new File(Role.SOUNDS_FOLDER + "loup_garou_blanc.wav")),
  LOUP_GAROU_AMNESIQUE(RoleClan.WOLFS, "Loup Garou Amnésique", new File(Role.SOUNDS_FOLDER + "loup_garou_amnesique.wav")),
  PETIT_LOUP_GAROU(RoleClan.WOLFS, "Vilain Petit Loup", new File(Role.SOUNDS_FOLDER + "petit_loup_garou.wav")),
  GRAND_MERE_LOUP(RoleClan.WOLFS, "Grand-Mère Loup", new File(Role.SOUNDS_FOLDER + "grand_mere_loup.wav"));

  public static final String SOUNDS_FOLDER = "sounds/";
  private static final List<Role> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  private RoleClan clan;
  private String name;
  private File voiceFile;

  private Role(RoleClan clan, String name, File voiceFile) {
    this.clan = clan;
    this.name = name;
    this.voiceFile = voiceFile;
  }
  
  public String getDescription() {
    switch(this) {
    case ANCIEN:
      return "Ancien : Il dispose de l’effet resistance 1, il sera ressuscité (1 seule fois). S’il est tué par un villageois, celui-ci perdra la moitié de sa vie.";
    case ASSASSIN:
      return "Assassin : Dispose de l’effet force 1 le jour et doit gagner seul. Il reçoit à l’annonces des rôles un livre power 3, un tranchant 3, un protection 3.";
    case CUPIDON:
      return "Cupidon : il reçoit 3 fils et 1 livre power 2 punch 1 et 64 flèches. A l’annonce " + 
          "de son rôle, il a 5mn pour choisir 2 personnes qui seront en couple (/lglove pseudo_1 pseudo_2, il ne peut pas se désigner. Il peut gagner avec les village ou les amoureux. Si un des amoureux meurt, l’autre meurt 30 seconds après. Dans un dernier élan de rage, il obtient force 1 + speed 1 avant de mourir.";
    case ENFANT_SAUVAGE:
      return "Enfant sauvage : Il choisit un modèle via une commande (/lgmodele pseudo du modèle) quand cette personne meurt, il devient loup et reçoit leur bonus et 2mn après la liste.";
    case GRAND_MERE_LOUP:
      return "Grand-mère loup : Quand spotted par un rôle (voyante, renard etc…) il apparaît comme simple villageois, cependant contrairement au loup amnésique, il est conscient de son rôle. Après qu’un joueur (non squelette) lui tirer une flèche, il apparaîtra chez la voyante ou renard comme un vrai loup garous. Elle dispose des pouvoirs classiques des loup garou."; 
    case INFECT_PERE_DES_LOUPS:
      return "L’infect père des loups : ils doit tuer tous les villageois, il dispose des bonus classiques des loups (night vision et force 1 la nuit) ainsi que la possibilité de réanimer un villageois mort par un loup une fois dans la partie avec la commande /lgsave nomDuJoueur, le joueur infecté conserve ses pouvoirs avant d’être tué.";
    case LOUP_GAROU:
      return "Loup-Garous : Les loups-Garous sont les ennemis des villageois, ils doivent tuer tous les membres du village. ils ont night vision 24/24 et force 1 la nuit.";
    case LOUP_GAROU_AMNESIQUE:
      if(GameData.isLoupAmnesiqueFound()) {
        return "Loup-Garou Amnésique : il est désigné Simple Villageois à l’annonce des rôles. Quand il est frappé par un loup-garou, un message lui apparaît lui annonçant son rôle loup-garou. Ses bonus de loup lui apparaissent donc et il reçoit la liste complète des pseudos des loups.";
      }else {
        return VILLAGEOIS.getDescription();
      }
    case LOUP_GAROU_BLANC:
      return "Loup-Garou Blanc : il a les mêmes pouvoir que les loups mais il doit gagner seul, il possède 15 coeurs contrairement aux autres joueurs.";
    case PETITE_FILLE:
      return "Petite Fille : elle reçoit l’effet night vision et invisibilité et faiblesse 1 durant la nuit. Elle reçoit aussi 5 TNT.";
    case PETIT_LOUP_GAROU:
      return "Vilain Petit Loup : il dispose de l’effet speed 1 la nuit ainsi que des pouvoirs classiques des loups. il doit gagner avec les autres loups";
    case RENARD:
      return "il dispose de l’effet speed 1, a moins de 10 blocs d’un autre joueur, il peut utiliser la commande /lgflairer “pseudo” pour savoir s’il est chez les innocents ou chez les loups (le loup amnésique non révélé et l'assassin sont annoncé comme innocents)";
    case SORCIERE:
      return "Sorcière : Elle reçoit 3 splash potion de vie 1, une splash de regen 1 et 3 potions d’instant damage 1. Elle peut ressusciter un joueur mort avec la commande /lgsauver “pseudo”";
    case VILLAGEOIS:
      return "Villageois : Vous n'avez malheureusement aucun pouvoir.";
    case VOYANTE:
      return "Voyante : À chaque début de journée (et à l’annonce de son rôle), il reçoit un message et obtient la possibilité de voir le rôle de qqn (/lgvoir ”pseudo”). Il reçoit 4 obsidienne et 4 bibliothèque elle dispose également de l’effet night vision.";
    default:
      return "Pas de description disponible :/";
    }
  }

  public static List<Role> getRolesWithConfig(GameConfig config){
    List<Role> roles = new ArrayList<>();

    for(int i = 0; i < config.getVillagersNumber(); i++) {
      roles.add(Role.VILLAGEOIS);
    }

    for(int i = 0; i < config.getSpecialVillagersNumber(); i++) {
      Role roleToAdd;
      do {
        roleToAdd = getRandomVillagerSpecialRole();
      }while(roles.contains(roleToAdd));
      roles.add(roleToAdd);
    }

    for(int i = 0; i < config.getWolfsNumber(); i++) {
      roles.add(Role.LOUP_GAROU);
    }

    for(int i = 0; i < config.getSpecialWolfsNumber(); i++) {
      Role roleToAdd;
      do {
        roleToAdd = getRandomWolfsSpecialRole();
      }while(roles.contains(roleToAdd));
      roles.add(roleToAdd);
    }

    return roles;
  }

  private static Role getRandomVillagerSpecialRole() {
    Role role;
    do {
      role = VALUES.get(RANDOM.nextInt(SIZE));
    }while((!role.getClan().equals(RoleClan.VILLAGE) && !role.getClan().equals(RoleClan.SPECIAL)) || role.equals(Role.VILLAGEOIS));

    return role;
  }

  private static Role getRandomWolfsSpecialRole() {
    Role role;
    do {
      role = VALUES.get(RANDOM.nextInt(SIZE));
    }while((!role.getClan().equals(RoleClan.WOLFS) && !role.getClan().equals(RoleClan.SPECIAL)) || role.equals(Role.LOUP_GAROU));

    return role;
  }

  public RoleClan getClan() {
    return clan;
  }

  public String getName() {
    return name;
  }

  public File getVoiceFile() {
    return voiceFile;
  }

}
