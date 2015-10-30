package ttr.main;

import ttr.model.player.HumanPlayer;
import ttr.model.player.Player;
import ttr.model.player.Skylar;
import ttr.view.scenes.TTRGamePlayScene;

public class TTRMain {

	public static void main(String[] args) {
		
		/* This is the game object required by the engine (essentially just the game window) */
		TicketToRide myGame = new TicketToRide();
		myGame.setFramesPerSecond(30);
		
		/* Initialize two players */
		Player player1 = new HumanPlayer("Human Player");
		Player player2 = new Skylar("Skylar");
		
		/*Optional: You can hide one or both of the player's stats (say, if you want to play against an AI without looking at their cards*/
		//player2.setHideStats(true);
		
		/* Set up the scene itself (with a background image and two players and a game object), do some initializing, and start the game */
		TTRGamePlayScene scene = new TTRGamePlayScene("Ticket To Ride", "woodBacking.jpg", myGame, player1, player2);
		myGame.setCurrentScene(scene);
		player1.setScene(scene);
		player2.setScene(scene);
		scene.setScaleX(.7);
		scene.setScaleY(.7);
		myGame.start();
		scene.playGame();
	}
}




