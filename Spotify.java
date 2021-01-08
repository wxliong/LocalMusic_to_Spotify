import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Runtime;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.File;
import java.sql.Timestamp;


public class Spotify {

  // IMPORTANT NOTE: music filename format -> [Artist Name] - [Song Title]
  // IMPORTANT NOTE: change playlist_uri to your own under method "runPOSTCommand"
  // side note (optional): import songs from your phone's local music storage to computer. e.g. C:\Music
  // side note (optional): try adding one song into your playlist using the commented lines within the main method
  // have to change access_token when it expires - https://developer.spotify.com/console/post-playlists/ (do refresh oauth token if you are not lazy)


  static String access_token = "BQAeA18NmgVMWll6yO2VMNGQREd1X1a0LadCpXQ60MwBHD_ywPmWKFtNoFtVqAekKKdPVzEartyouaYXah1bl10Yx3NDvgpD3DYvRE7PiKXciYCVXXzmmGSAqfW79xuCv9UMn4hRpmVpu8r30xPlddimJL2aLrOUzCCFJrlz20khwe_gElS2qBcRFS8s70L4EVZY3dTQ8yA-Iec8RCN3u4o0ODbvPFXw";
  static String artist;
  static String track;
  static String artist_name;
  static String track_name;
  // change path accordingly
  // side note: some songs may have to be added manually -> failed.txt contains songs that are unsuccessful 
  static String failed = "D:/Music1/failed.txt";
  static String success = "D:/Music1/success.txt";
  static String error = "D:/Music1/error.txt";
  static Timestamp timestamp = new Timestamp(System.currentTimeMillis());

  public static void main(String[] args) throws IOException {
    Files.deleteIfExists(Paths.get(failed));
    Files.deleteIfExists(Paths.get(success));
    Files.deleteIfExists(Paths.get(error));
    // change path accordingly
    String csvFile = "D:/Music1/Songs.csv";
    BufferedReader br = null;
    String line = "";
    String csvSplitBy = ",";

    createFailedStatusFile(failed);
    createSuccessStatusFile(success);

    try {
      br = new BufferedReader(new FileReader(csvFile));
      while ((line = br.readLine()) != null) {

        String[] songs = line.split(csvSplitBy);
        artist = songs[0];
        track = songs[1];
        artist_name = artist.replaceAll(" ", "%20");
        track_name = track.replaceAll(" ", "%20");
        runGETCommand(artist_name, track_name);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    System.out.println("Successfully wrote to the success.txt");
    System.out.println("Successfully wrote to the failed.txt");

    //// Uncomment the lines below to test if music can be added into your playlist 
    
    // Scanner sc = new Scanner(System.in);
    // System.out.println("Artist Name:");
    // artist_name = sc.nextLine();
    // System.out.println("Track Name:");
    // track_name = sc.nextLine();
    // sc.close();
    // artist_name = artist_name.replaceAll(" ", "%20");
    // track_name = track_name.replaceAll(" ", "%20");
    // runGETCommand(artist_name, track_name);
  }

  public static void runGETCommand(String artist_name, String track_name) throws IOException {
    Runtime rt = Runtime.getRuntime();
    String url = "\"" + "https://api.spotify.com/v1/search?q=track:" + track_name + "%20artist:" + artist_name
        + "&type=track&limit=1" + "\"";
    String cmdString = "curl -X GET " + url + " -H " + "\"" + "Authorization: Bearer " + access_token + "\"";
    // for testing purposes 
    // System.out.println(cmdString);
    Process pr = rt.exec(cmdString);
    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
    getTrackURI(input);
  }

  public static void getTrackURI(BufferedReader input) throws IOException {
    String line = null;
    while ((line = input.readLine()) != null) {
      // for testing purposes 
      // System.out.println(line);
      Pattern song_found = Pattern.compile("spotify:track:[^" + "\"" + "]*");
      Pattern song_not_found = Pattern.compile("\"" + "total" + "\"" + " : 0");
      Pattern token_expired = Pattern.compile("The access token expired");
      Matcher matcher = song_found.matcher(line);
      Matcher matcher1 = song_not_found.matcher(line);
      Matcher matcher2 = token_expired.matcher(line);

      if (matcher.find()) {
        String track_uri = matcher.group(0);
        runPOSTCommand(track_uri);

      } else if (matcher1.find()) {
        try {
          FileWriter failed_tracks = new FileWriter(failed, true);
          failed_tracks.write(artist + " - " + track + "\n");
          failed_tracks.close();
        } catch (IOException e) {
          System.out.println("failed.txt had an error occured!");
        }
      } else if (matcher2.find()) {
        FileWriter error_output = new FileWriter(error);
        error_output.write(timestamp + " - Access Token Has Expired!");
        System.out.println(timestamp + " - Access Token Has Expired!");
        error_output.close();
        System.exit(0);
      }
    }

  }

  public static void runPOSTCommand(String track_uri) throws IOException {
    Runtime rt = Runtime.getRuntime();
    // change playlist_uri here - Step 1: in your spotify playlist -> Step 2: click the 3 dots button -> Step 3: share -> Step 4: copy Spotify URI
    String playlist_uri = "4skeuWnQrwEE44Y9fHfvKF";
    String url = "\"" + "https://api.spotify.com/v1/playlists/" + playlist_uri + "/tracks?uris=" + track_uri + "\"";
    String cmdString = "curl -i -X POST " + url + " -H " + "\"" + "Authorization: Bearer " + access_token + "\""
        + " -H " + "\"" + "Accept: application/json" + "\"" + " -d 0";
    // for testing purposes 
    // System.out.println(cmdString);
    Process pr = rt.exec(cmdString);
    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
    addTracktoSpotify(input);
  }

  public static void addTracktoSpotify(BufferedReader input) throws IOException {
    String line = null;
    while ((line = input.readLine()) != null) {
      Pattern song_found = Pattern.compile("snapshot_id");
      Matcher matcher = song_found.matcher(line);
      // for testing purposes 
      // System.out.println(line);
      if (matcher.find()) {
        try {
          FileWriter success_tracks = new FileWriter(success, true);
          success_tracks.write(artist + " - " + track + "\n");
          success_tracks.close();
        } catch (IOException e) {
          System.out.println("success.txt had an error occured");
        }
      }
      // System.out.println(line);
    }
  }

  public static void createFailedStatusFile(String filename) throws IOException {
    try {
      File failed = new File(filename);
      if (failed.createNewFile()) {
        System.out.println("File Created: " + failed.getName());
      } else {
        System.out.println("File already exists");
      }
    } catch (IOException e) {
      System.out.println("An Error Occured");
      e.printStackTrace();
    }
  }

  public static void createSuccessStatusFile(String filename) throws IOException {
    try {
      File success = new File(filename);
      if (success.createNewFile()) {
        System.out.println("File Created: " + success.getName());
      } else {
        System.out.println("File already exists");
      }
    } catch (IOException e) {
      System.out.println("An Error Occured");
      e.printStackTrace();
    }
  }
}