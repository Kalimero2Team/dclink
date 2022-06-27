package com.kalimero2.team.dclink.api;

public final class DCLinkApiHolder {
    private static DCLinkApi api;

    public static boolean set(DCLinkApi api) {
        if (DCLinkApiHolder.api == null) {
            DCLinkApiHolder.api = api;
            return true;
        }
        return false;
    }

    public static DCLinkApi getApi() {
        return api;
    }
}
