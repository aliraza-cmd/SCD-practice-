
package persian.model;

public class GlossaryEntry {

    private int glossaryEntryId;
    private int chapterId;
    private String persianWord;
    private String urduMeaning;

    public GlossaryEntry() {
    }

    public GlossaryEntry(int glossaryEntryId, int chapterId, String persianWord, String urduMeaning) {
        this.glossaryEntryId = glossaryEntryId;
        this.chapterId = chapterId;
        this.persianWord = persianWord;
        this.urduMeaning = urduMeaning;
    }

    public int getGlossaryEntryId() {
        return glossaryEntryId;
    }

    public void setGlossaryEntryId(int glossaryEntryId) {
        this.glossaryEntryId = glossaryEntryId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getPersianWord() {
        return persianWord;
    }

    public void setPersianWord(String persianWord) {
        this.persianWord = persianWord;
    }

    public String getUrduMeaning() {
        return urduMeaning;
    }

    public void setUrduMeaning(String urduMeaning) {
        this.urduMeaning = urduMeaning;
    }
}
