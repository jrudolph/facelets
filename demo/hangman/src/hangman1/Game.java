/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman1;

/**
 * Tracks the players progress through the game, including guessed letters and
 * incorrect guesses remaining.
 * 
 * @author Howard Lewis Ship
 */
public class Game
{
    private String _targetWord;
    private int _incorrectGuessesLeft;
    private char[] _letters;
    private boolean[] _guessed = new boolean[26];
    private boolean _win;

    /**
     *  Returns true if the player has guessed all letters in the word.
     * 
     */

    public boolean isWin()
    {
        return _win;
    }

    /**
     *  Returns an array of letters that have been guessed by the player, with
     *  an underscore for each unguessed position.  Once the player loses,
     *  this returns the array of actual letters in the target word.
     * 
     *  <p>
     *  The caller must not modify this array.
     * 
     */

    public char[] getLetters()
    {
        return _letters;
    }

    /**
     *  Returns the number of incorrect guesses remaining.
     *  An incorrect guess when this is already zero results
     *  in a loss.
     * 
     */

    public int getIncorrectGuessesLeft()
    {
        return _incorrectGuessesLeft;
    }

    /**
     *  Returns an array of flags indicating which letters have already been guessed.
     *  There are 26 flags, one for each letter, starting with 'A' at index 0, up to 'Z' at
     *  index 25.
     * 
     *  <p>The caller must not modify this array.
     * 
     */

    public boolean[] getGuessedLetters()
    {
        return _guessed;
    }

    /**
     *  Initializes the Game with a new target word.  This resets the
     *  incorrectGuessesLeft count, and initializes the array of
     *  letters and letters guessed. 
     * 
     */


    public void start(String word)
    {
        _targetWord = word;
        _incorrectGuessesLeft = 5;
        _win = false;

        int count = word.length();

        _letters = new char[count];

        for (int i = 0; i < count; i++)
            _letters[i] = '_';

        for (int i = 0; i < 26; i++)
            _guessed[i] = false;
    }
		
    /**
     *  The player makes a guess.  If the letter has already been 
     *  guessed, then no change occurs.  Otherwise, there's a check 
     *  to see if the guess fills in any positions in the target word.
     *  This may result in a win.  If the guess doesn't match any
     *  letter of the word, then a failure occurs; when enough 
     *  failures occur, the game results in a loss.
     * 
     *  @return true if further guesses are allowed (this guess did 
     *  not result in a win or a loss), or false if further guesses 
     *  are not allowed (the player guessed the word, or used up
     *  all possible incorrect guesses).
     * 
     */

    public boolean makeGuess(char letter)
    {
        char ch = Character.toLowerCase(letter);

        if (ch < 'a' || ch > 'z')
            throw new IllegalArgumentException("Must provide an alphabetic character.");

        int index = ch - 'a';

        // If the player (somehow) guesses the same letter more than once, it does not affect
        // state of the game.

        if (_guessed[index])
            return true;

        _guessed[index] = true;

        boolean good = false;
        boolean complete = true;

        for (int i = 0; i < _letters.length; i++)
        {
            if (_letters[i] != '_')
                continue;

            if (_targetWord.charAt(i) == ch)
            {
                good = true;
                _letters[i] = ch;
                continue;
            }

            // An empty slot that does not match
            // the guess, so the word is not
            // complete.

            complete = false;
        }

        if (good)
        {
            _win = complete;

            return !complete;
        }


        if (_incorrectGuessesLeft == 0)
        {
            // Replace the letters array with the solution

            _letters = _targetWord.toCharArray();

            return false;
        }

        _incorrectGuessesLeft--;

        // Not a good guess, but not a loss yet

        return true;
    }
}
