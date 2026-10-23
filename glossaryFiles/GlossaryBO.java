package persian.glossary;

import java.util.List;

import persian.dal.IDALFacade;
import persian.model.Chapter;
import persian.model.GlossaryEntry;

public class GlossaryBO {

    private final IDALFacade dalFacade;

    public GlossaryBO(IDALFacade dalFacade) {
        this.dalFacade = dalFacade;
    }

    public List<Chapter> getChapters() {
        return dalFacade.findAllChapters();
    }

    public List<GlossaryEntry> getEntries(int chapterId) {
        return dalFacade.findGlossaryEntriesByChapterId(chapterId);
    }

    public GlossaryEntry getEntry(int glossaryEntryId) {
        return dalFacade.findGlossaryEntryById(glossaryEntryId);
    }

    public void addEntry(int chapterId, String persianWord, String urduMeaning) {
        validate(persianWord, urduMeaning);
        if (chapterId <= 0 || dalFacade.findChapterById(chapterId) == null) {
            throw new IllegalArgumentException("The selected chapter does not exist.");
        }
        GlossaryEntry entry = new GlossaryEntry(0, chapterId, persianWord.trim(), urduMeaning.trim());
        if (!dalFacade.saveGlossaryEntry(entry)) {
            throw new IllegalStateException("Could not save the glossary entry.");
        }
    }

    public void updateEntry(int glossaryEntryId, int chapterId, String persianWord, String urduMeaning) {
        validate(persianWord, urduMeaning);
        GlossaryEntry entry = new GlossaryEntry(glossaryEntryId, chapterId, persianWord.trim(), urduMeaning.trim());
        if (!dalFacade.updateGlossaryEntry(entry)) {
            throw new IllegalStateException("Could not update the glossary entry.");
        }
    }

    public void deleteEntry(int glossaryEntryId) {
        if (!dalFacade.deleteGlossaryEntry(glossaryEntryId)) {
            throw new IllegalStateException("Could not delete the glossary entry.");
        }
    }

    private void validate(String persianWord, String urduMeaning) {
        if (persianWord == null || persianWord.trim().isEmpty()) {
            throw new IllegalArgumentException("Persian word is required.");
        }
        if (urduMeaning == null || urduMeaning.trim().isEmpty()) {
            throw new IllegalArgumentException("Urdu meaning is required.");
        }
    }
}
