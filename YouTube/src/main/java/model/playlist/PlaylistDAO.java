package model.playlist;

import model.db.DBManager;
import model.exceptions.PlaylistException;
import model.video.Video;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistDAO {

    private static PlaylistDAO instance = new PlaylistDAO();

    private PlaylistDAO() {
    }

    public static PlaylistDAO getInstance() {
        return instance;
    }

    public void createPlaylist(Playlist playlist) throws PlaylistException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "insert into youtube.playlists " +
                    "(title, date_created, owner_id) " +
                     "values (?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, playlist.getTitle());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(playlist.getDate_created()));
            preparedStatement.setInt(3, playlist.getOwner_id());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int result_id = resultSet.getInt(1);
            playlist.setId(result_id);
        } catch (SQLException e) {
            throw new PlaylistException("Playlist could not be created.", e);
        }
    }

    public void editPlaylist(Playlist playlist, String title) throws PlaylistException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "update youtube.playlists set title = ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            playlist.setTitle(title);
            preparedStatement.setString(1, playlist.getTitle());
            preparedStatement.setInt(2, playlist.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw  new PlaylistException("Could not edit playlist. Please, try again later.", e);
        }

    }

    public void deletePlaylist(Playlist playlist) throws PlaylistException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String deleteVideosSql = "delete from videos_in_playlist where playlist_id = ?;";
            String deletePlaylistSql = "delete from youtube.playlists where id = ?;";
            try(PreparedStatement deleteAllVideos = connection.prepareStatement(deleteVideosSql);
            PreparedStatement deletePlaylist = connection.prepareStatement(deletePlaylistSql)){

                connection.setAutoCommit(false);

                deleteAllVideos.setInt(1, playlist.getId());
                deleteAllVideos.executeUpdate();

                deletePlaylist.setInt(1, playlist.getId());
                deletePlaylist.executeUpdate();

                connection.commit();
            }catch (SQLException e) {
                connection.rollback();
                throw  new PlaylistException("Something went wrong with deleting the playlist", e);
            }
            finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
           throw  new PlaylistException("Could not delete playlist", e);
        }
    }

    public void addVideoToPlaylist(Video video, Playlist playlist) throws PlaylistException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "insert into youtube.videos_in_playlist values (?,?, now());";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, video.getId());
            preparedStatement.setInt(2, playlist.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw  new PlaylistException("Could not add video to playlist.", e);
        }
    }

    public void removeVideoFromPlaylist(Video video, Playlist playlist) throws PlaylistException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "delete from youtube.videos_in_playlist where video_id = ? and playlist_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, video.getId());
            preparedStatement.setInt(2, playlist.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw  new PlaylistException("Could not remove video from playlist", e);
        }
    }

    public List<Video> getAllVideosFromPlaylist(Playlist playlist) throws PlaylistException {
        try{
            List<Video> videos = new ArrayList <>();
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "select *\n" +
                    "from youtube.videos as v\n" +
                    "join youtube.videos_in_playlist as vp\n" +
                    "on v.id = vp.video_id\n" +
                    "where vp.playlist_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, playlist.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Video video = new Video(resultSet.getInt("id"), resultSet.getString("title"),
                        resultSet.getString("description"), resultSet.getString("video_url"),
                        resultSet.getString("thumbnail_url"), resultSet.getLong("duration"),
                        resultSet.getTimestamp("date_uploaded").toLocalDateTime(),
                        resultSet.getInt("owner_id"),
                        resultSet.getInt("category_id"));
                videos.add(video);
            }
            return Collections.unmodifiableList(videos);
        } catch (SQLException e) {
            throw  new PlaylistException("Could not view all videos from playlist", e);
        }
    }

    public List<Playlist> getPlaylistsByTitle(String title) throws PlaylistException {
        try {
            List<Playlist> playlists = new ArrayList <>();
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "select * from youtube.playlists where title = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Playlist playlist = new Playlist(resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getTimestamp("date_created").toLocalDateTime(),
                        resultSet.getInt("owner_id"));
                playlists.add(playlist);
            }
            return Collections.unmodifiableList(playlists);
        } catch (SQLException e) {
            throw new PlaylistException("Could not get playlist by title", e);
        }
    }
}
