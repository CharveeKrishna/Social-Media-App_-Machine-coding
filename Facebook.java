import java.util.*;

// Represents a post in the Facebook system
class Post {
    private Integer id; // Unique identifier for the post
    private Integer time; // Timestamp for the post creation
    private Post prev; // Previous post in the doubly linked list
    private Post next; // Next post in the doubly linked list

    // Default constructor
    public Post() {
    }

    // Constructor to initialize post with an ID and timestamp
    public Post(Integer id) {
        this.id = id;
        this.time = Facebook.timestamp++;
    }

    // Getters and setters for the post attributes
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Post getPrev() {
        return prev;
    }

    public void setPrev(Post prev) {
        this.prev = prev;
    }

    public Post getNext() {
        return next;
    }

    public void setNext(Post next) {
        this.next = next;
    }
}

// Represents a user in the Facebook system
class User {
    private Integer userId; // Unique identifier for the user
    private Set<Integer> followed; // Set of user IDs this user follows
    private Map<Integer, Post> postMap; // Map of posts created by the user
    private Post head; // Head of the doubly linked list of posts
    private Post tail; // Tail of the doubly linked list of posts

    // Getters and setters for the user attributes
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Set<Integer> getFollowed() {
        return followed;
    }

    public void setFollowed(Set<Integer> followed) {
        this.followed = followed;
    }

    public Map<Integer, Post> getPostMap() {
        return postMap;
    }

    public void setPostMap(Map<Integer, Post> postMap) {
        this.postMap = postMap;
    }

    public Post getHead() {
        return head;
    }

    public void setHead(Post head) {
        this.head = head;
    }

    public Post getTail() {
        return tail;
    }

    public void setTail(Post tail) {
        this.tail = tail;
    }

    // Constructor to initialize a user with a user ID
    public User(Integer userId) {
        this.userId = userId;
        this.followed = new HashSet<>();
        follow(userId); // A user follows themselves by default
        this.postMap = new HashMap<>();
        this.head = new Post(-1); // Sentinel head node
        this.tail = new Post(-1); // Sentinel tail node
        this.head.setNext(tail); // Initialize doubly linked list
        this.tail.setPrev(head);
    }

    // Method to follow another user
    public void follow(Integer userId) {
        followed.add(userId);
    }

    // Method to unfollow another user
    public void unfollow(Integer userId) {
        followed.remove(userId);
    }

    // Method to create a post
    public void createPost(Integer postId) {
        Post post = new Post(postId); // Create a new post
        postMap.put(postId, post); // Add post to user's post map
        Post next = head.getNext();
        head.setNext(post);
        next.setPrev(post);

        post.setPrev(head);
        post.setNext(next);
    }

    // Method to delete a post
    public void deletePost(Integer postId) {
        Post post = postMap.get(postId);
        if (post == null) {
            return; // Return if post doesn't exist
        }
        postMap.remove(postId); // Remove post from post map
        Post prev = post.getPrev();
        Post next = post.getNext();

        prev.setNext(next); // Update linked list pointers
        next.setPrev(prev);
    }
}

// Represents the Facebook system
public class Facebook {
    public static int timestamp; // Global timestamp for post creation
    public static Map<Integer, User> userMap; // Map of users in the system
    public static Integer PAGE_SIZE; // Number of posts per page in paginated feed
    public static Integer FEED_SIZE; // Number of posts in the news feed

    // Constructor to initialize Facebook system
    public Facebook() {
        timestamp = 0; // Initialize timestamp
        userMap = new HashMap<>(); // Initialize user map
        PAGE_SIZE = 2; // Default page size
        FEED_SIZE = 10; // Default feed size
    }

    // Method to create a post for a user
    public void createPost(Integer userId, Integer postId) {
        User user = userMap.get(userId);
        if (user == null) {
            user = new User(userId); // Create new user if not exists
            userMap.put(userId, user);
        }
        user.createPost(postId); // Create post
        System.out.println("User " + userId + " posted " + postId);
    }

    // Method to delete a post for a user
    public void deletePost(Integer userId, Integer postId) {
        User user = userMap.get(userId);
        if (user == null) {
            user = new User(userId); // Create new user if not exists
            userMap.put(userId, user);
        }
        user.deletePost(postId); // Delete post
        System.out.println("User " + userId + " deleted post " + postId);
    }

    // Method to follow another user
    public void follow(Integer userId, Integer followeeId) {
        User follower = userMap.get(userId);
        User followee = userMap.get(followeeId);
        if (follower == null) {
            follower = new User(userId); // Create new user if not exists
            userMap.put(userId, follower);
        }
        if (followee == null) {
            followee = new User(followeeId); // Create new user if not exists
            userMap.put(followeeId, followee);
        }
        follower.follow(followeeId); // Follow the user
        System.out.println("User " + userId + " followed User " + followeeId);
    }

    // Method to unfollow another user
    public void unfollow(Integer userId, Integer followeeId) {
        User follower = userMap.get(userId);
        User followee = userMap.get(followeeId);
        if (follower == null) {
            follower = new User(userId); // Create new user if not exists
            userMap.put(userId, follower);
        }
        if (followee == null) {
            followee = new User(followeeId); // Create new user if not exists
            userMap.put(followeeId, followee);
        }
        follower.unfollow(followeeId); // Unfollow the user
        System.out.println("User " + userId + " unfollowed User " + followeeId);
    }

    // Method to get the news feed for a user
    public void getNewsFeed(Integer userId) {
        List<Integer> feed = fetchTopNPosts(userId, FEED_SIZE); // Fetch top N posts
        System.out.println("Feed for user " + userId);
        for (int i = 0; i < feed.size(); i++)
            System.out.println("Post " + (i + 1) + " " + feed.get(i));
    }

    // Method to get paginated news feed for a user
    public void getNewsFeedPaginated(Integer userId, Integer pageNumber) {
        User user = userMap.get(userId);
        if (user == null)
            return;
        List<Integer> feed = fetchTopNPosts(userId, Integer.MAX_VALUE); // Fetch all posts
        Integer start = pageNumber * PAGE_SIZE;
        Integer end = Math.min(start + PAGE_SIZE, feed.size());
        if (start >= end)
            return;
        List<Integer> paginatedFeed = feed.subList(start, end); // Get paginated posts
        System.out.println("Page number " + pageNumber + " of user " + userId + " feed");
        for (int i = 0; i < paginatedFeed.size(); i++)
            System.out.println("Post " + (i + 1) + " " + paginatedFeed.get(i));
    }

    // Helper method to fetch top N posts for a user
    private List<Integer> fetchTopNPosts(Integer userId, int N) {
        User user = userMap.get(userId);
        if (user == null)
            return new LinkedList<>();
        int n = 0;
        List<Integer> posts = new LinkedList<>();
        Set<Integer> followed = user.getFollowed();
        PriorityQueue<Post> pq = new PriorityQueue<>((a, b) -> (b.getTime() - a.getTime()));
        for (Integer currUserId : followed) {
            User currUser = userMap.get(currUserId);
            Post head = currUser.getHead();
            Post tail = currUser.getTail();
            if (head.getNext() != tail)
                pq.add(head.getNext());
        }

        while (!pq.isEmpty() && n < N) {
            Post curr = pq.poll();
            n++;
            posts.add(curr.getId());
            if (curr.getNext().getId() != -1)
                pq.add(curr.getNext());
        }
        return posts;
    }

    // Main method to test the Facebook system
    public static void main(String[] args) {
        Facebook facebook = new Facebook();
        facebook.follow(1, 2);
        facebook.follow(1, 3);
        facebook.follow(1, 4);
        facebook.follow(1, 5);
        facebook.follow(1, 6);
        facebook.follow(1, 7);
        facebook.follow(1, 8);
        facebook.follow(1, 9);
        facebook.follow(1, 10);
        facebook.follow(1, 11);
        facebook.follow(1, 12);
        facebook.follow(1, 13);
        facebook.createPost(1, 1000);
        facebook.createPost(2, 1002);
        facebook.createPost(3, 1003);
        facebook.createPost(4, 1004);
        facebook.createPost(5, 1005);
        facebook.createPost(6, 1006);
        facebook.createPost(7, 1007);
        facebook.createPost(8, 1008);
        facebook.createPost(9, 1009);
        facebook.createPost(10, 1010);
        facebook.createPost(11, 1011);
        facebook.createPost(12, 1012);
        facebook.createPost(13, 1013);
        facebook.getNewsFeed(1);
        facebook.unfollow(1, 13);
        facebook.getNewsFeed(1);
        facebook.deletePost(12, 1012);
        facebook.getNewsFeed(1);
        facebook.getNewsFeedPaginated(1, 2);
        facebook.getNewsFeedPaginated(1, 5);
    }
}
