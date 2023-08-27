package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        userPlaylistMap.put(user,new ArrayList<>());
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist,new ArrayList<>());
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artistKey=createArtist(artistName);

        for (Album album:albums){
            if(album.getTitle().equals(title)){
                return album;
            }
        }
        Album tempAlbum=new Album(title);
        albums.add(tempAlbum);


        List<Album> tempAlbumList = artistAlbumMap.getOrDefault(artistKey,new ArrayList<>());
        tempAlbumList.add(tempAlbum);
        artistAlbumMap.put(artistKey,tempAlbumList);
        return tempAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album albumKey=null;
        for (Album a: albums){
            if(albumName.equals(a.getTitle())){
                albumKey=a;
                break;
            }
        }

        if(albumKey==null) throw new Exception("Album does not exist");

//        creating song
        Song song = new Song(title, length);
        songs.add(song);

//        putting in album
        List<Song> songList=albumSongMap.getOrDefault(albumKey,new ArrayList<>());
        songList.add(song);
        albumSongMap.put(albumKey,songList);
        return song;
    }

    public User getUser(String mobile){
        User currUser=null;
        for (User a:users) {
            if (mobile.equals(a.getMobile())){
                currUser=a;
                break;
            }
        }
        return currUser;
    }
    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        for (Playlist p:playlists){
            if(title.equals(p.getTitle())){
                return p;
            }
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

//        list of song having given length
        List<Song> songOfGiveLength= new ArrayList<>();
        for(Song s:songs){
            if(length==s.getLength()){
                songOfGiveLength.add(s);
            }
        }

        playlistSongMap.put(playlist,songOfGiveLength);

        User currUser=getUser(mobile);
        if(currUser==null){
            throw new Exception("User does not exist");
        }
        List<User> listOfListener=playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        listOfListener.add(currUser);
        playlistListenerMap.put(playlist,listOfListener);
        creatorPlaylistMap.put(currUser,playlist);
        List<Playlist> listOfPlaylist=userPlaylistMap.getOrDefault(currUser,new ArrayList<>());
        listOfPlaylist.add(playlist);
        userPlaylistMap.put(currUser,listOfPlaylist);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for(Playlist p:playlists){
            if(p.equals(p.getTitle())){
                return p;
            }
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        //songs
        List<Song> songOfGiveName =new ArrayList<>();
        for (Song s: songs){
            if(songTitles.contains(s.getTitle())){
                songOfGiveName.add(s);
            }
        }

//        playlist - list of song map
        playlistSongMap.put(playlist,songOfGiveName);

//        user
        User currUser=getUser(mobile);

        if(currUser==null){
            throw new Exception("User does not exist");
        }

//        playlist -list of listener map
        List<User> listOfListener = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        listOfListener.add(currUser);
        playlistListenerMap.put(playlist,listOfListener);

        //creator -playlist map
        creatorPlaylistMap.put(currUser,playlist);

        //user-list of playlist map

        List<Playlist> listOfPlaylist=userPlaylistMap.getOrDefault(currUser,new ArrayList<>());
        listOfPlaylist.add(playlist);
        userPlaylistMap.put(currUser,listOfPlaylist);

        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User currUser= getUser(mobile);

        if (currUser==null){
            throw new Exception("User does not exist");
        }
//        check for playlist
        Playlist currPlaylist =null;
        for (Playlist p:playlists){
            if (p.getTitle().equals(playlistTitle)){
                currPlaylist=p;
                break;
            }
        }

        if (currPlaylist==null){
            throw new Exception("Playlist does not exist");
        }

//        listener playlist
        List<User> tempList=playlistListenerMap.getOrDefault(currPlaylist,new ArrayList<>());
        if (!tempList.contains(currUser)){
            tempList.add(currUser);
            playlistListenerMap.put(currPlaylist,tempList);
        }

//        user playlist
        List<Playlist> temp2Playlist = userPlaylistMap.getOrDefault(currUser,new ArrayList<>());
        if (!temp2Playlist.contains(currPlaylist)){
            temp2Playlist.add(currPlaylist);
            userPlaylistMap.put(currUser,temp2Playlist);
        }

        return currPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User currUser=getUser(mobile);
        if (currUser==null){
            throw new Exception("User does not exist");
        }

//        check for song existance
        Song currSong=null;
        for (Song s:songs){
            if(songTitle.equals(s.getTitle())){
                currSong=s;
                break;
            }
        }
        if (currSong==null){
            throw new Exception("Song does not exist");
        }

        List<User> likesList=songLikeMap.getOrDefault(currSong,new ArrayList<>());

        if(!likesList.contains(currUser)){
            likesList.add(currUser);
            songLikeMap.put(currSong,likesList);
            currSong.setLikes(currSong.getLikes()+1);

//            song - album
            Album currAlbum=null;
            for (Album a:albumSongMap.keySet()){
                if (albumSongMap.get(a).contains(currSong)){
                    currAlbum=a;
                    break;
                }
            }

//            album-artist
            Artist currArtist=null;
            for(Artist a:artistAlbumMap.keySet()){
                if (artistAlbumMap.get(a).contains(currAlbum)){
                    currArtist=a;
                    break;
                }
            }

            assert  currArtist !=null;
            currArtist.setLikes(currArtist.getLikes()+1);

        }
        return  currSong;

    }

    public String mostPopularArtist() {
        int maxLikes=0;
        String result="";
        for (Artist a: artists){
            if (a.getLikes()>maxLikes){
                maxLikes=a.getLikes();
                result=a.getName();
            }
        }
        return  result;
    }

    public String mostPopularSong() {
        int maxLikes =0 ;
        String result="";
        for (Song a:songs){
            if (a.getLikes()>maxLikes){
                maxLikes=a.getLikes();
                result=a.getTitle();
            }
        }
        return result;
    }
}
