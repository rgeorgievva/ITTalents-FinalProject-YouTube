package model.comment;

import model.db.DBManager;
import model.video.Video;

import java.sql.*;
import java.time.LocalDateTime;

public class CommentDAO {

    private static CommentDAO instance = new CommentDAO();

    public static CommentDAO getInstance() {
        return instance;
    }

    private CommentDAO(){}

    public void addCommentToVideo(Video video, Comment comment){
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "insert into youtube.comments " +
                    "(text, time_posted, video_id, owner_id) values" +
                    "(?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, comment.getText());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(comment.getTime_posted()));
                comment.setVideo_id(video.getId());
                preparedStatement.setInt(3, (int) comment.getVideo_id());
                preparedStatement.setInt(4, (int) comment.getOwner_id());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                long comment_id = resultSet.getInt(1);
                comment.setId(comment_id);
        }catch (SQLException e) {
            System.out.println("Could not add comment to the database");
        }
    }

    public void addReplyToComment(Comment parentComment, Comment comment){
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String sql = "insert into youtube.comments " +
                    "(text, time_posted, video_id, owner_id, replied_to_id) values" +
                    "(?,?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, comment.getText());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(comment.getTime_posted()));
                comment.setVideo_id(parentComment.getVideo_id());
                preparedStatement.setInt(3, (int) comment.getVideo_id());
                preparedStatement.setInt(4, (int) comment.getOwner_id());
                comment.setReplied_to_id(parentComment.getId());
                preparedStatement.setInt(5, (int) comment.getReplied_to_id());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                long comment_id = resultSet.getInt(1);
                comment.setId(comment_id);
        } catch (SQLException e) {
            System.out.println("Could not add reply to the database");
        }
    }

         public void editComment(String editedText, Comment comment){
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            comment.setText(editedText);
            String sql = "update youtube.comments set text = ? where id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setString(1, comment.getText());
                preparedStatement.setInt(2, (int) comment.getId());
                preparedStatement.executeUpdate();
            }
            catch (SQLException e){
                System.out.println("Comment couldn't be edited");
            }
        } catch (SQLException e) {
            System.out.println("Connection to the database could not not established");
        }
    }


}
