package li.pitschmann.knx.core.plugin.api.v1.json;

/**
 * JSON response about *.knxproj Project structure
 */
public final class ProjectStructureResponse {
    private String id;
    private String name;
    private int version;
    private String groupAddressStyle;
    private int numberOfGroupAddresses;
    private int numberOfGroupRanges;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getGroupAddressStyle() {
        return groupAddressStyle;
    }

    public void setGroupAddressStyle(String groupAddressStyle) {
        this.groupAddressStyle = groupAddressStyle;
    }

    public int getNumberOfGroupAddresses() {
        return numberOfGroupAddresses;
    }

    public void setNumberOfGroupAddresses(int numberOfGroupAddresses) {
        this.numberOfGroupAddresses = numberOfGroupAddresses;
    }

    public int getNumberOfGroupRanges() {
        return numberOfGroupRanges;
    }

    public void setNumberOfGroupRanges(int numberOfGroupRanges) {
        this.numberOfGroupRanges = numberOfGroupRanges;
    }
}
