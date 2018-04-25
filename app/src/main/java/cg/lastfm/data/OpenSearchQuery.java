package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

class OpenSearchQuery {
    @SerializedName("#text")
    public String text;

    public String role;
    public String searchTerms;
    public long startPage;
}
