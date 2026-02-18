package tools;

public class playerJSON {

    private String UUID;
    private String name;

    // ban
    private String ip; // only for ban-ip
    private String created;
    private String source;
    private String expires;

    // op
    private String level;
    private String bypassesPlayerLimit;

    // Constructor for whitelist members
    public playerJSON(String UUID, String name) {
        this.UUID = UUID;
        this.name = name;
    }
    // Constructor for banned-ips
    public playerJSON(String ip, String created, String source, String expires, String bannedips, String nullString) {
        this.ip = ip;
        this.created = created;
        this.source = source;
        this.expires = expires;
    }
    // Constructor for banned players
    public playerJSON(String UUID, String name, String source, String expires, String created) {
        this.UUID = UUID;
        this.name = name;
        this.source = source;
        this.expires = expires;
        this.created = created;
    }
    // Constructor for OP players
    public playerJSON(String UUID, String name, String level, String bypassesPlayerLimit) {
        this.UUID = UUID;
        this.name = name;
        this.level = level;
        this.bypassesPlayerLimit = bypassesPlayerLimit;
    }

    public String getUUID() {
        return UUID;
    }

    public String getName() {
        return name;
    }

    public String getExpires() { return expires; }
    public void setExpires(String expires) { this.expires = expires; }
    public String getBypassesPlayerLimit() { return bypassesPlayerLimit; }
    public void setBypassesPlayerLimit(String bypassesPlayerLimit) { this.bypassesPlayerLimit = bypassesPlayerLimit; }

    @Override
    public String toString() {
        return this.getUUID() + ": " + this.getName();
    }

    // getJSONS
    public String getWhitelistJSON() {
        return """
                  {
                    "uuid": "%s",
                    "name": "%s"
                  }""".formatted(this.UUID,this.name);
    }
    public String getBannedIPJSON() {
        return """
                  {
                    "ip": "%s",
                    "created": "%s",
                    "source": "%s",
                    "expires": "%s"
                  }""".formatted(this.ip,this.created,this.source,this.expires);
    }
    public String getBannedPlayerJSON() {
        return """
                  {
                    "uuid": "%s",
                    "name": "%s",
                    "created": "%s",
                    "source": "%s",
                    "expires": "%s"
                  }""".formatted(this.UUID, this.name, this.created, this.source, this.expires);
    }
    public String getAdminPlayersJSON() {
        return """
                  {
                    "uuid": "%s",
                    "name": "%s",
                    "level": %s,
                    "bypassesPlayerLimit": %s
                  }""".formatted(this.UUID,this.name, this.level, this.bypassesPlayerLimit);
    }
}
