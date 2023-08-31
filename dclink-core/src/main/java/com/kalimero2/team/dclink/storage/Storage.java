package com.kalimero2.team.dclink.storage;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.impl.discord.DiscordAccountImpl;
import com.kalimero2.team.dclink.impl.minecraft.MinecraftPlayerImpl;
import org.intellij.lang.annotations.Language;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Storage {
    private final DCLink dcLink;
    private Connection connection;

    public Storage(DCLink dcLink, File dataBase) {
        this.dcLink = dcLink;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBase.getPath());
            createTablesIfNotExists();

        } catch (ClassNotFoundException | SQLException e) {
            dcLink.getLogger().error("Error while creating database connection", e);
        }

    }

    private void createTablesIfNotExists() throws SQLException {
        createDiscordTableIfNotExists();
        createMinecraftTableIfNotExists();
        updateMinecraftTable();
        dcLink.getLogger().info("Database initialized");
    }

    private ResultSet executeQuery(@Language(value = "SQL") String query, Object... args) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
        }
        return statement.executeQuery();
    }

    private void createDiscordTableIfNotExists() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS DISCORD_ACCOUNTS (" +
                        "    DISCORD_ID TEXT PRIMARY KEY NOT NULL UNIQUE" +
                        ");"
        );
        statement.close();
    }

    private void createMinecraftTableIfNotExists() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS MINECRAFT_ACCOUNTS(" +
                        "    UUID               TEXT PRIMARY KEY NOT NULL UNIQUE," +
                        "    LAST_KNOWN_NAME    TEXT NULL," +
                        "    DISCORD_ID         TEXT NULL," +
                        "    FOREIGN KEY (DISCORD_ID) REFERENCES DISCORD_ACCOUNTS(DISCORD_ID)" +
                        ");"
        );
        statement.close();
    }

    private void updateMinecraftTable() throws SQLException {
        Statement checkStatement = connection.createStatement();
        ResultSet resultSet = checkStatement.executeQuery("PRAGMA table_info(MINECRAFT_ACCOUNTS);");
        boolean hasLastKnownName = false;
        while (resultSet.next()) {
            if (resultSet.getString("name").equalsIgnoreCase("last_known_name")) {
                hasLastKnownName = true;
                break;
            }
        }
        if (!hasLastKnownName) {
            dcLink.getLogger().info("Updating MINECRAFT_ACCOUNTS table");
            Statement addStatement = connection.createStatement();
            addStatement.executeUpdate(
                    "ALTER TABLE MINECRAFT_ACCOUNTS ADD COLUMN LAST_KNOWN_NAME TEXT NULL;"
            );
            addStatement.close();
        }
    }

    private void saveDiscordAccount(DiscordAccount discordAccount) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT OR IGNORE INTO DISCORD_ACCOUNTS (DISCORD_ID) VALUES ('" + discordAccount.getId() + "');");
        statement.close();
    }

    private void saveMinecraftPlayer(MinecraftPlayer minecraftPlayer) throws SQLException {
        Statement statement = connection.createStatement();
        String name = minecraftPlayer.getName();
        String uuid = minecraftPlayer.getUuid().toString();
        String discordId;
        if (minecraftPlayer.getDiscordAccount() != null) {
            discordId = minecraftPlayer.getDiscordAccount().getId();
        } else {
            discordId = null;
        }
        statement.executeUpdate("INSERT OR IGNORE INTO MINECRAFT_ACCOUNTS (UUID, DISCORD_ID, LAST_KNOWN_NAME) VALUES ('" + uuid + "', '" + discordId + "', '" + name + "' );");
        statement.close();
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            dcLink.getLogger().error("Error while closing database connection", e);
        }
    }

    public String getLastKnownName(UUID uuid) throws SQLException {
        ResultSet resultSet = executeQuery("SELECT LAST_KNOWN_NAME FROM MINECRAFT_ACCOUNTS WHERE UUID = ?", uuid.toString());
        if (resultSet.next()) {
            return resultSet.getString("LAST_KNOWN_NAME");
        }
        resultSet.close();
        return null;
    }

    public DiscordAccount getDiscordAccount(String discordID) throws SQLException {
        if (discordID == null || discordID.isEmpty() || discordID.equals("null")) {
            return null;
        }

        List<MinecraftPlayer> linkedPlayers = new ArrayList<>();

        DiscordAccountImpl discordAccount = new DiscordAccountImpl(dcLink, discordID) {
            @Override
            public Collection<MinecraftPlayer> getLinkedPlayers() {
                return linkedPlayers.stream().toList();
            }
        };

        ResultSet resultSet = executeQuery("SELECT UUID FROM MINECRAFT_ACCOUNTS WHERE DISCORD_ID = ?", discordID);
        while (resultSet.next()) {
            linkedPlayers.add(new MinecraftPlayerImpl(dcLink, UUID.fromString(resultSet.getString("UUID"))) {
                @Override
                public DiscordAccount getDiscordAccount() {
                    return discordAccount;
                }
            });
        }
        resultSet.close();


        saveDiscordAccount(discordAccount);
        return discordAccount;
    }

    public UUID getUUIDByLastKnownName(String username) throws SQLException {
        ResultSet resultSet = executeQuery("SELECT UUID FROM MINECRAFT_ACCOUNTS WHERE LAST_KNOWN_NAME = ?", username);
        if (resultSet.next()) {
            return UUID.fromString(resultSet.getString("UUID"));
        }
        resultSet.close();
        return null;
    }


    public MinecraftPlayer getMinecraftPlayer(UUID uuid) throws SQLException {
        ResultSet resultSet = executeQuery("SELECT DISCORD_ID FROM MINECRAFT_ACCOUNTS WHERE UUID = ?", uuid.toString());
        MinecraftPlayerImpl minecraftPlayer = null;
        if (resultSet.next()) {
            String discordID = resultSet.getString("DISCORD_ID");
            if (discordID != null) {
                DiscordAccount discordAccount = getDiscordAccount(discordID);
                minecraftPlayer = new MinecraftPlayerImpl(dcLink, uuid) {
                    @Override
                    public DiscordAccount getDiscordAccount() {
                        return discordAccount;
                    }
                };
            }
        }
        if (minecraftPlayer == null) {
            minecraftPlayer = new MinecraftPlayerImpl(dcLink, uuid) {
                @Override
                public DiscordAccount getDiscordAccount() {
                    return null;
                }
            };
        }
        resultSet.close();
        saveMinecraftPlayer(minecraftPlayer);
        return minecraftPlayer;
    }

    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE MINECRAFT_ACCOUNTS SET DISCORD_ID = ? WHERE UUID = ?;");
            pstmt.setString(1, discordAccount.getId());
            pstmt.setString(2, minecraftPlayer.getUuid().toString());
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            dcLink.getLogger().error("Error while linking accounts", e);
        }
        return false;
    }

    public void unLinkAccounts(DiscordAccount discordAccount) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE MINECRAFT_ACCOUNTS SET DISCORD_ID = NULL WHERE DISCORD_ID = ?;");
            pstmt.setString(1, discordAccount.getId());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            dcLink.getLogger().error("Error while unlinking accounts", e);
        }
    }

    public void unLinkAccount(MinecraftPlayer minecraftPlayer) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE MINECRAFT_ACCOUNTS SET DISCORD_ID = NULL WHERE UUID = ?;");
            pstmt.setString(1, minecraftPlayer.getUuid().toString());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            dcLink.getLogger().error("Error while unlinking account", e);
        }
    }

}
