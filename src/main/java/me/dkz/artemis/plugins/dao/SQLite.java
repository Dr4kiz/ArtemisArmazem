package me.dkz.artemis.plugins.dao;

import com.google.gson.Gson;
import me.dkz.artemis.plugins.ArtemisArmazem;
import me.dkz.artemis.plugins.storage.PlotItem;
import me.dkz.artemis.plugins.storage.PlotStorage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SQLite {

    private final ArtemisArmazem plugin = ArtemisArmazem.getInstance();
    private final PlotStorage plotStorage = plugin.getPlotStorage();



    public SQLite() {

        try (Connection connection = openConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS storages (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT NOT NULL, data TEXT NOT NULL);");
            preparedStatement.execute();
            plugin.getLogger().info("Tabela do banco de dados criada com sucesso.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Não foi possivel criar a conexão com o banco de dados.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        File file = new File(plugin.getDataFolder(), "storage.db");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Não foi possivel criar o arquivo do banco de dados.");
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        }

        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + file);
    }

    public void save(UUID uuid, Set<PlotItem> items) {
        try (
                Connection connection = openConnection()
        ) {
            Gson gson = new Gson();
            PreparedStatement exist = connection.prepareStatement("SELECT * FROM storages WHERE `uuid` = ?");
            exist.setString(1, uuid.toString());
            ResultSet rs = exist.executeQuery();
            if (rs.next()) {
                PreparedStatement update = connection.prepareStatement("UPDATE storages SET `data` = ? WHERE `uuid` = ?");
                update.setString(1, gson.toJson(items));
                update.setString(2, uuid.toString());
                update.executeUpdate();
            } else {
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storages(uuid, data) VALUES (?, ?)");
                insert.setString(1, uuid.toString());
                insert.setString(2, gson.toJson(items));
                insert.executeUpdate();
            }


        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("Não foi possivel criar a conexão com o banco de dados.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public void load() {
        String sql = "SELECT * FROM storages";
        try (Connection connection = openConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            Gson gson = new Gson();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                List<PlotItem> plotItems = stringToArray(resultSet.getString("data"), PlotItem[].class);

                plotItems.forEach(item ->{
                    plotStorage.addToStorage(uuid, item.getType(), item.getAmount());
                });
            }
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("Não foi possivel criar a conexão com o banco de dados.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }

}
