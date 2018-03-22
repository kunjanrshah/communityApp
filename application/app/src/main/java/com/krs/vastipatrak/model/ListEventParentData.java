package com.krs.vastipatrak.model;

public class ListEventParentData {

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getEventTitle() {
        return EventTitle;
    }

    public void setEventTitle(String eventTitle) {
        EventTitle = eventTitle;
    }

    public String getEventDesc() {
        return EventDesc;
    }

    public void setEventDesc(String eventDesc) {
        EventDesc = eventDesc;
    }

    public String getEventLocation() {
        return EventLocation;
    }

    public void setEventLocation(String eventLocation) {
        EventLocation = eventLocation;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    String EventId;
    String EventTitle;
    String EventDesc;
    String EventLocation;
    String EventDate;
}
