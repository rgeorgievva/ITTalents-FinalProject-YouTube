package model.comment;

import model.db.DBManager;
import model.exceptions.CommentException;
import model.user.User;
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
                preparedStatement.setTimestamp(2, Timestamp.valueOf(comment.getTimePosted()));
                comment.setVideoId(video.getId());
                preparedStatement.setInt(3, comment.getVideoId());
                preparedStatement.setInt(4,  comment.getOwnerId());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                int comment_id = resultSet.getInt(1);
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
                preparedStatement.setTimestamp(2, Timestamp.valueOf(comment.getTimePosted()));
                comment.setVideoId(parentComment.getVideoId());
                preparedStatement.setInt(3, comment.getVideoId());
                preparedStatement.setInt(4,  comment.getOwnerId());
                comment.setRepliedToId(parentComment.getId());
                preparedStatement.setInt(5,  comment.getRepliedToId());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                int comment_id = resultSet.getInt(1);
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
                preparedStatement.setInt(2, comment.getId());
                preparedStatement.executeUpdate();
        } catch (SQLException e) {
           throw new CommentException("Could not edit comment. Please, try again later.", e);
        }
    }

    public void deleteComment(Comment comment) throws CommentException {
        try{
            Connection connection = DBManager.INSTANCE.getConnection();
            String deleteFromComments = "delete from youtube.comments where id = ? or replied_to_id = ?;";

            try (PreparedStatement deleteFromCommentsStatement = connection.prepareStatement(deleteFromComments);) {

                connection.setAutoCommit(false);

                deleteFromCommentsStatement.setInt(1, comment.getId());
                deleteFromCommentsStatement.setInt(2, comment.getId());
                deleteFromCommentsStatement.executeUpdate();

                connection.commit();

            }catch (SQLException e) {
                connection.rollback();
                throw new CommentException("Something went wrong with deleting comment or its reactions", e);
            }
            finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new CommentException("Comment couldn't be deleted", e);
        }
    }

    public void likeComment(User user, Comment comment) throws CommentException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            if(commentIsAlreadyLiked(user, comment)){
                String unlike = "delete from youtube.users_liked_comments where user_id = ? and comment_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(unlike);
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, comment.getId());
                preparedStatement.executeUpdate();
            }
            else {
                if(commentIsAlreadyDisliked(user, comment)){
                    String sql = "delete from youtube.users_disliked_comments where user_id = ? and comment_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, user.getId());
                    preparedStatement.setInt(2, comment.getId());
                    preparedStatement.executeUpdate();
                }
                String like = "insert into youtube.users_liked_comments values (? , ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(like);
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, comment.getId());
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new CommentException("Could not like comment. Please, try again later.",e);
        }
    }

    public void dislikeComment(User user, Comment comment) throws CommentException {
        try {
            Connection connection = DBManager.INSTANCE.getConnection();
            if(commentIsAlreadyDisliked(user, comment)){
                String sql = "delete from youtube.users_disliked_comments where user_id = ? and comment_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, comment.getId());
                preparedStatement.executeUpdate();
            }
            else {
                if(commentIsAlreadyLiked(user, comment)){
                    String unlike = "delete from youtube.users_liked_comments where user_id = ? and comment_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(unlike);
                    preparedStatement.setInt(1, user.getId());
                    preparedStatement.setInt(2, comment.getId());
                    preparedStatement.executeUpdate();
                }
                String dislike = "insert into youtube.users_disliked_comments values (? , ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(dislike);
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, comment.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CommentException("Could not dislike comment. Please, try again later.",e);
        }
    }

    //finds if the current comment is already liked
    private boolean commentIsAlreadyLiked(User user, Comment comment) throws SQLException {
        Connection connection = DBManager.INSTANCE.getConnection();
        String sql = "select * from youtube.users_liked_comments where user_id = ? and comment_id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, comment.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    //finds if the current comment is already disliked
    private boolean commentIsAlreadyDisliked(User user, Comment comment) throws SQLException {
        Connection connection = DBManager.INSTANCE.getConnection();
        String sql = "select * from youtube.users_disliked_comments where user_id = ? and comment_id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, comment.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }


}
