package com.zuehlke.pgadmissions.domain.helpers;

public enum NotificationListTestScenario {
    
    TASKREMINDERSUCCESS(new NotificationListTestCase[]{NotificationListTestCase.DUETASKREMINDER}),
    TASKREMINDERFAILURE(new NotificationListTestCase[]{NotificationListTestCase.RECEIVEDRECENTTASKNOTIFICATION, NotificationListTestCase.NOTIFICATIONWINDOWEXPIRED, 
            NotificationListTestCase.NOURGENTACTIONS, NotificationListTestCase.ACTIONNOTSYNDICATED, NotificationListTestCase.USERACCOUNTDISABLED, NotificationListTestCase.USERACCOUNTEXPIRED,
            NotificationListTestCase.USERACCOUNTLOCKED, NotificationListTestCase.USERCREDENTIALSEXPIRED}),
    TASKNOTIFICATIONSUCCESS(new NotificationListTestCase[]{NotificationListTestCase.DUETASKNOTIFICATIONNEVERRECEIVED, NotificationListTestCase.DUETASKNOTIFICATIONRECEIVEDAGESAGO}),
    TASKNOTIFICATIONFAILURE(new NotificationListTestCase[]{NotificationListTestCase.RECEIVEDRECENTTASKNOTIFICATION, NotificationListTestCase.NOTIFICATIONWINDOWEXPIRED, 
            NotificationListTestCase.NOURGENTACTIONS, NotificationListTestCase.ACTIONNOTSYNDICATED, NotificationListTestCase.USERACCOUNTDISABLED, NotificationListTestCase.USERACCOUNTEXPIRED,
            NotificationListTestCase.USERACCOUNTLOCKED, NotificationListTestCase.USERCREDENTIALSEXPIRED}),
    UPDATENOTIFICATIONSUCCESS(new NotificationListTestCase[]{NotificationListTestCase.DUEUPDATENOTIFICATIONNEVERRECEIVED, NotificationListTestCase.DUEUPDATENOTIFICATIONRECEIVEDAGESAGO}),
    UPDATENOTIFICATIONFAILURE(new NotificationListTestCase[]{NotificationListTestCase.RECIEVEDRECENTUPDATENOTIFICATION, NotificationListTestCase.ROLENOTSUBSCRIBEDTOUPDATES, 
            NotificationListTestCase.NOUPDATES, NotificationListTestCase.OLDUPDATES, NotificationListTestCase.USERACCOUNTDISABLED, NotificationListTestCase.USERACCOUNTEXPIRED, 
            NotificationListTestCase.USERACCOUNTLOCKED, NotificationListTestCase.USERCREDENTIALSEXPIRED});
    
    private NotificationListTestCase[] displayValue;

    private NotificationListTestScenario(NotificationListTestCase[] displayValue) {
        this.setDisplayValue(displayValue);
    }

    public NotificationListTestCase[] getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(NotificationListTestCase[] displayValue) {
        this.displayValue = displayValue;
    }

}