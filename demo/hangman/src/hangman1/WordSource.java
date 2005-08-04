/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *  Used by {@link hangman1.Game} to obtain a a random word.
 *
 *  @author Howard Lewis Ship
 */

public class WordSource
{
    private int _nextWord;
    private List _words = new ArrayList();

    public WordSource()
    {
        readWords();
    }

    private void readWords()
    {

        try
        {
            InputStream in = getClass().getResourceAsStream("WordList.txt");
            Reader r = new InputStreamReader(in);
            LineNumberReader lineReader = new LineNumberReader(r);

            while (true)
            {
                String line = lineReader.readLine();

                if (line == null)
                    break;

                if (line.startsWith("#"))
                    continue;

                String word = line.trim().toLowerCase();

                if (word.length() == 0)
                    continue;

                _words.add(word);
            }

            lineReader.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Unable to read list of words from file WordList.txt.", ex);
        }

        // Randomize the word order

        Collections.shuffle(_words);

    }

    /**
     *  Gets the next random word from the list.  Once the list is exhausted, it
     *  is shuffled and the first word is taken; this ensures that the user won't
     *  see a repeat word until all words in the list have been played.
     * 
     **/

    public String nextWord()
    {
        if (_nextWord >= _words.size())
        {
            _nextWord = 0;
            Collections.shuffle(_words);
        }

        return (String) _words.get(_nextWord++);
    }
}
