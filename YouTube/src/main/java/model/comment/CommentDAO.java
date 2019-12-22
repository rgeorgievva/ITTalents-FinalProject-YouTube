package model.comment;

import model.db.DBManager;
import model.exceptions.CommentException;
import model.video.Video;

import java.sql.*;

public class CommentDAO {

    private static CommentDAO instance = new CommentDAO();

    public static CommentDAO getInstance() {
        return instance;
    }

    private CommentDAO(){}

    public void addCommentToVideo(Video video, Comment comment) throws CommentException {
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
            throw new CommentException("Could not add comment to video. Please, try again later.", e);
        }
    }

    public void addReplyToComment(Comment parentComment, Comment comment) throws CommentException{
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
            throw  new CommentException("Could not add reply to comment. Please, try again later.", e);
        }
    }

    public void editComment(String editedText, Comment comment) throws CommentException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            comment.setText(editedText);
            String sql = "update youtube.comments set text = ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, comment.getText());
                preparedStatement.setInt(2, (int) comment.getId());
                preparedStatement.executeUpdate();
        } catch (SQLException e) {
           throw new CommentException("Could not edit comment. Please, try again later.", e);
        }
    }

    public void deleteComment(Comment comment) throws CommentException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            String setFKChecksToZero = "set FOREIGN_KEY_CHECKS = 0;";
            PreparedStatement preparedStatementFKZero = connection.prepareStatement(setFKChecksToZero);
            preparedStatementFKZero.executeUpdate();
            String sql = "delete from youtube.comments where id = ? or replied_to_id = ?;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setInt(1, (int) comment.getId());
                preparedStatement.setInt(2, (int) comment.getId());
                preparedStatement.executeUpdate();
            }
            String setFKChecksToOne = "set FOREIGN_KEY_CHECKS = 1;";
            PreparedStatement preparedStatementFKOne = connection.prepareStatement(setFKChecksToOne);
            preparedStatementFKOne.executeUpdate();
        } catch (SQLException e) {
            throw new CommentException("Comment couldn't be deleted");
        }
    }

}
