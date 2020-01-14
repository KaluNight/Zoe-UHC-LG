package ch.kalunight.uhclg.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Role {
  ANGE(RoleClan.VILLAGE, "Ange"),
  VOYANTE(RoleClan.VILLAGE, "Voyante"),
  CUPIDON(RoleClan.VILLAGE, "Cupidon"),
  PETITE_FILLE(RoleClan.VILLAGE, "Petite Fille"),
  SORCIERE(RoleClan.VILLAGE, "Sorcière"),
  ANCIEN(RoleClan.VILLAGE, "Ancien"),
  RENARD(RoleClan.VILLAGE, "Renard"),
  CHAMAN(RoleClan.VILLAGE, "Chaman"),
  VILLAGEOIS(RoleClan.VILLAGE, "Villageois"),
  ENFANT_SAUVAGE(RoleClan.SPECIAL, "Enfant Sauvage"),
  ASSASSIN(RoleClan.SPECIAL, "Assassin"),
  LOUP_GAROU(RoleClan.WOLFS, "Loup Garou"),
  INFECT_PERE_DES_LOUPS(RoleClan.WOLFS, "Infect Père des Loups Garou"),
  LOUP_GAROU_BLANC(RoleClan.WOLFS, "Loup Garou Blanc");

  private static final List<Role> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  private RoleClan clan;
  private String name;

  private Role(RoleClan clan, String name) {
    this.clan = clan;
    this.name = name;
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

}
