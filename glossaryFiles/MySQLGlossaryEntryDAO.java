package persian.dal.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persian.dal.dao.IGlossaryEntryDAO;
import persian.dal.database.DatabaseConnection;
import persian.model.GlossaryEntry;

public class MySQLGlossaryEntryDAO implements IGlossaryEntryDAO {

    @Override
    public boolean saveGlossaryEntry(GlossaryEntry entry) {
        String sql = "INSERT INTO glossary_entry (chapter_id, persian_word, urdu_meaning) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, entry.getChapterId());
            statement.setString(2, entry.getPersianWord());
            statement.setString(3, entry.getUrduMeaning());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        entry.setGlossaryEntryId(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public GlossaryEntry findGlossaryEntryById(int glossaryEntryId) {
        String sql = "SELECT glossary_entry_id, chapter_id, persian_word, urdu_meaning "
                + "FROM glossary_entry WHERE glossary_entry_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, glossaryEntryId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    GlossaryEntry entry = new GlossaryEntry();
                    entry.setGlossaryEntryId(result.getInt("glossary_entry_id"));
                    entry.setChapterId(result.getInt("chapter_id"));
                    entry.setPersianWord(result.getString("persian_word"));
                    entry.setUrduMeaning(result.getString("urdu_meaning"));
                    return entry;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<GlossaryEntry> findGlossaryEntriesByChapterId(int chapterId) {
        List<GlossaryEntry> entries = new ArrayList<GlossaryEntry>();
        String sql = "SELECT glossary_entry_id, chapter_id, persian_word, urdu_meaning "
                + "FROM glossary_entry WHERE chapter_id = ? ORDER BY glossary_entry_id";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, chapterId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    GlossaryEntry entry = new GlossaryEntry();
                    entry.setGlossaryEntryId(result.getInt("glossary_entry_id"));
                    entry.setChapterId(result.getInt("chapter_id"));
                    entry.setPersianWord(result.getString("persian_word"));
                    entry.setUrduMeaning(result.getString("urdu_meaning"));
                    entries.add(entry);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    @Override
    public boolean updateGlossaryEntry(GlossaryEntry entry) {
        String sql = (entry.getChapterId() > 0)
                ? "UPDATE glossary_entry SET chapter_id = ?, persian_word = ?, urdu_meaning = ? WHERE glossary_entry_id = ?"
                : "UPDATE glossary_entry SET persian_word = ?, urdu_meaning = ? WHERE glossary_entry_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            if (entry.getChapterId() > 0) {
                statement.setInt(1, entry.getChapterId());
                statement.setString(2, entry.getPersianWord());
                statement.setString(3, entry.getUrduMeaning());
                statement.setInt(4, entry.getGlossaryEntryId());
            } else {
                statement.setString(1, entry.getPersianWord());
                statement.setString(2, entry.getUrduMeaning());
                statement.setInt(3, entry.getGlossaryEntryId());
            }

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteGlossaryEntry(int glossaryEntryId) {
        String sql = "DELETE FROM glossary_entry WHERE glossary_entry_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, glossaryEntryId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteGlossaryEntriesByChapterId(int chapterId) {
        String sql = "DELETE FROM glossary_entry WHERE chapter_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, chapterId);

            return statement.executeUpdate() >= 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}