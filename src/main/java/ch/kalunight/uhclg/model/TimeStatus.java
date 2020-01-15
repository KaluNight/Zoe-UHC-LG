package ch.kalunight.uhclg.model;

public enum TimeStatus {
  DAY("Jour"),
  NIGHT("Nuit");
  
  private String name;
  
  private TimeStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
