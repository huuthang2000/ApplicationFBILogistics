package com.example.demoapp.model;

public class GroupChatList {
    String groupId, groupTitle, groupDescription, groupIcon, timestamp, createdBy, timemessage;

    public  GroupChatList(){}

    public GroupChatList(String groupId, String groupTitle, String groupDescription, String groupIcon,
                         String timestamp, String createdBy, String timemessage) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.groupIcon = groupIcon;
        this.timestamp = timestamp;
        this.createdBy = createdBy;
        this.timemessage = timemessage;
    }

    public String getTimemessage() {
        return timemessage;
    }

    public void setTimemessage(String timemessage) {
        this.timemessage = timemessage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
