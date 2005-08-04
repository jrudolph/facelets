/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman1;

/**
 *
 *  The Visit class runs most of the game logic and acts as controller, mediating
 *  between the pure interface code and the pure logic code.
 *
 *  @author Howard Lewis Ship (original Tapestry version)
 *  @author Hand Bergsten (modified for JSF example)
 */

public class Visit
{
    // In a real application, the word source would be shared between all sessions.  
    // Here, we just allow each Visit to have its own instance.

    private WordSource _wordSource = new WordSource();

    // On the other hand, the Game is specifically for this
    // Visit and only this Visit.

    private Game _game = new Game();

    // Converted to a JSF action method
    public String startGame()
    {
        _game.start(_wordSource.nextWord());

        // Now that the Game is initialized, we can go to the Guess 
        // page to allow the player to start making guesses.

	// Note! in the JSF version, the page selection is handled
	// by a navigation rule in faces-config.xml based on the
	// outcome value.

        return "newGame";
    }

    /**
     *  Processes the player's guess, possibly updating the response
     *  page to be "Win" or "Lose".
     * 
     **/
    // Converted to a JSF action method, invoked from GuessBean
    public String makeGuess(char ch)
    {

        // If this return true, then stay on this page at let
        // player keep guessing.

        if (_game.makeGuess(ch))
            return "notDone";

	return _game.isWin() ? "win" : "lose";
    }

    /**
     *  Returns the {@link Game} instance for this Visit; this is used
     *  primarily by the {@link Guess} page to display things like
     *  the number of remaining guesses and the list of guessed
     *  and unguessed letters.
     * 
     **/

    public Game getGame()
    {
        return _game;
    }

    /** 
     * Added as a bridge to Game.getLetters(), turning the char[] into
     * a String[], because the JSF UIData component doesn't handle
     * primitive type arrays (and String is easier to compare with in
     * an EL expression). This is a JSF flaw that will hopefully be
     * fixed in a future version.
     */
    public String[] getLetters()
    {
	char[] letters = _game.getLetters();
	String[] wrappedLetters = new String[letters.length];
	for (int i = 0; i < letters.length; i++) {
	    wrappedLetters[i] = String.valueOf(letters[i]);
	}
	return wrappedLetters;
    }

    /**
     * Added for JSF version. Returns an array of GuessBean instances
     * with one element per guess.
     */
    public GuessBean[] getGuesses()
    {
	boolean[] guessed = _game.getGuessedLetters();
	GuessBean[] guesses = new GuessBean[guessed.length];
	for (int i = 0; i < guessed.length; i++) {
	    if (guessed[i]) {
		guesses[i] = new GuessBean('#', this);
	    }
	    else {
		guesses[i] = new GuessBean((char) ('a' + i), this);
	    }
	}
	return guesses;
    }

    /**
     * Added for the JSF version. A bean that holds a letter and an
     * action method for guessing the letter.
     */
    public static class GuessBean {
	private char c;
	private Visit v;

	public GuessBean(char c, Visit v) {
	    this.c = c;
	    this.v = v;
	}

        public String getLetter() {
	    return String.valueOf(c);
	}

	public String makeGuess() {
	    return v.makeGuess(c);
	}
    }
}
