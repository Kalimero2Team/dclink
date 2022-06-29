package com.kalimero2.team.dclink.storage;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.impl.discord.DiscordAccountImpl;
import com.kalimero2.team.dclink.impl.minecraft.MinecraftPlayerImpl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
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

    public Storage(DCLink dcLink, File dataBase){
        this.dcLink = dcLink;

        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBase.getPath());
            createTablesIfNotExists();

        }catch (ClassNotFoundException | SQLException e) {
            dcLink.getLogger().error("Error while creating database connection", e);
        }

    }

    private void createTablesIfNotExists() throws SQLException {
        createDiscordTableIfNotExists();
        createMinecraftTableIfNotExists();
        dcLink.getLogger().info("Database initialized");
    }

    private void createDiscordTableIfNotExists() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("" +
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
                "    UUID       TEXT PRIMARY KEY NOT NULL UNIQUE," +
                "    DISCORD_ID TEXT," +
                "    FOREIGN KEY (DISCORD_ID) REFERENCES DISCORD_ACCOUNTS(DISCORD_ID)" +
                ");"
        );
        statement.close();
    }

    private void saveDiscordAccount(DiscordAccount discordAccount) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT OR IGNORE INTO DISCORD_ACCOUNTS (DISCORD_ID) VALUES ('" + discordAccount.getId() + "');");
        statement.close();
    }

    private void saveMinecraftPlayer(MinecraftPlayer minecraftPlayer) throws SQLException {
        Statement statement = connection.createStatement();
        String discordId;
        if(minecraftPlayer.getDiscordAccount() != null){
            discordId = minecraftPlayer.getDiscordAccount().getId();
        }else {
            discordId = null;
        }

        statement.executeUpdate("INSERT OR IGNORE INTO MINECRAFT_ACCOUNTS (UUID, DISCORD_ID) VALUES ('" + minecraftPlayer.getUuid() + "', '" + discordId + "');");
        statement.close();
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            dcLink.getLogger().error("Error while closing database connection", e);
        }
    }

    public DiscordAccount getDiscordAccount(String discordID) throws SQLException {

        List<MinecraftPlayer> linkedPlayers = new ArrayList<>();

        DiscordAccountImpl discordAccount = new DiscordAccountImpl(dcLink, discordID) {
            @Override
            public Collection<MinecraftPlayer> getLinkedPlayers() {
                return linkedPlayers.stream().toList();
            }
        };

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT UUID FROM MINECRAFT_ACCOUNTS WHERE DISCORD_ID = '" + discordID + "';");

        while(resultSet.next()){
            linkedPlayers.add(new MinecraftPlayerImpl(dcLink, UUID.fromString(resultSet.getString("UUID"))) {
                @Override
                public DiscordAccount getDiscordAccount() {
                    return discordAccount;
                }
            });
        }


        saveDiscordAccount(discordAccount);
        return discordAccount;
    }


    public MinecraftPlayer getMinecraftPlayer(UUID uuid) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISCORD_ID FROM MINECRAFT_ACCOUNTS WHERE UUID = '" + uuid.toString() + "';");
        MinecraftPlayerImpl minecraftPlayer = null;
        if(resultSet.next()){
            String discordID = resultSet.getString("DISCORD_ID");
            DiscordAccount discordAccount = getDiscordAccount(discordID);
            minecraftPlayer = new MinecraftPlayerImpl(dcLink, uuid) {
                @Override
                public DiscordAccount getDiscordAccount() {
                    return discordAccount;
                }
            };
        }
        if(minecraftPlayer == null){
            minecraftPlayer = new MinecraftPlayerImpl(dcLink, uuid) {
                @Override
                public DiscordAccount getDiscordAccount() {
                    return null;
                }
            };
        }

        saveMinecraftPlayer(minecraftPlayer);
        return minecraftPlayer;
    }

    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE MINECRAFT_ACCOUNTS SET DISCORD_ID = '" + discordAccount.getId() + "' WHERE UUID = '" + minecraftPlayer.getUuid().toString() + "';");
            statement.close();
            return true;
        } catch (SQLException e) {
            dcLink.getLogger().error("Error while linking accounts", e);
        }
        return false;
    }


}
