package util;

import model.User;

// Shared by ALL members — stores the logged-in user
public class Session {
    private static User currentUser;

    public static void set(User u)   { currentUser = u; }
    public static User get()         { return currentUser; }
    public static void clear()       { currentUser = null; }
}
