package ch.kalunight.uhclg.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Role {
  /*ANGE(RoleClan.VILLAGE, "Ange"),*/
  VOYANTE(RoleClan.VILLAGE),
  CUPIDON(RoleClan.VILLAGE),
  PETITE_FILLE(RoleClan.VILLAGE),
  SORCIERE(RoleClan.VILLAGE),
  ANCIEN(RoleClan.VILLAGE),
  RENARD(RoleClan.VILLAGE),
  VILLAGEOIS(RoleClan.VILLAGE),
  ASSASSIN(RoleClan.SPECIAL),
  LOUP_GAROU(RoleClan.WOLFS),
  ENFANT_SAUVAGE(RoleClan.WOLFS),
  INFECT_PERE_DES_LOUPS(RoleClan.WOLFS),
  LOUP_GAROU_BLANC(RoleClan.WOLFS),
  LOUP_GAROU_AMNESIQUE(RoleClan.WOLFS),
  PETIT_LOUP_GAROU(RoleClan.WOLFS),
  GRAND_MERE_LOUP(RoleClan.WOLFS);

  public static final String SOUNDS_FOLDER = "sounds/";
  private static final List<Role> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  private RoleClan clan;

  private Role(RoleClan clan) {
    this.clan = clan;
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

}
