/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui.listener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A SelectKeyInputListener can ensure that a text box always has a well defined content.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SelectKeyInputListener implements KeyboardListener {

    //~ Instance fields --------------------------------------------------------

    private char[] key = { 'd', 'd', 'd', 'd', '-', 'd', 'd', '-', 'd', 'd' };
//    private String exp = "\\d{4}-\\d{1,2}-\\d{1,2}";
    private String lastValidText = "";

    //~ Constructors -----------------------------------------------------------

    /**
     * The key parameter is the regular expression. that determine the strings, which are allowed by this
     * SelectKeyInputListener.
     *
     * @param  key  The key can consists some of the following characters:
     *
     *              <ul>
     *                <li>d a digit</li>
     *                <li>- a - (minus) character</li>
     *                <li>. a . (dot) character</li>
     *                <li>+ the characters, which are specified with the last key can exist more often than one time,
     *                  but at least one time</li>
     *              </ul>
     */
    public SelectKeyInputListener(final char[] key) {
        this.key = key;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void onKeyDown(final Widget sender, final char keyCode, final int modifiers) {
    }

    @Override
    public void onKeyPress(final Widget sender, final char keyCode, final int modifiers) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param   character  DOCUMENT ME!
     * @param   key        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean match(final char character, final char key) {
        if (key == 'd') {
            return Character.isDigit(character);
        } else {
            return character == key;
        }
    }

    @Override
    public void onKeyUp(final Widget sender, final char keyCode, final int modifiers) {
        if (sender instanceof TextBox) {
            final TextBox box = (TextBox)sender;
            String tmpText;
            final int cursorPos = box.getCursorPos();

            tmpText = box.getText();

            int it = 0;
            int ik = 0;
            for (; (it < tmpText.length()) && (ik < key.length); ++it, ++ik) {
                if (key[ik] == 'd') {
                    if (!Character.isDigit(tmpText.charAt(it))) {
                        break;
                    }
                } else if (key[ik] == '-') {
                    if (tmpText.charAt(it) != '-') {
                        break;
                    }
                } else if (key[ik] == ':') {
                    if (tmpText.charAt(it) != ':') {
                        break;
                    }
                } else if (key[ik] == '.') {
                    if (tmpText.charAt(it) != '.') {
                        break;
                    }
                } else if (key[ik] == 'K') {
                    if ((tmpText.charAt(it) != '.') && (tmpText.charAt(it) != ',')) {
                        break;
                    }
                    if (tmpText.charAt(it) == ',') {
                        final StringBuilder b = new StringBuilder(tmpText);
                        b.setCharAt(it, '.');
                        tmpText = b.toString();
                        box.setText(tmpText);
                    }
                } else if (key[ik] == '+') {
                    if ((ik > 0) && match(tmpText.charAt(it), key[ik - 1])) {
                        --ik;
                    } else {
                        --it;
                    }
//                    else if ( (ik + 1) < key.length && match(tmpText.charAt(it), key[ik + 1]) ) {
//                        ++ik;
//                    } else {
//                        break;
//                    }
                }
            }

            if (it != tmpText.length()) {
                Scheduler.get().scheduleDeferred(new Command() {

                        @Override
                        public void execute() {
                            box.setText(lastValidText);
                            box.setCursorPos(
                                ((cursorPos > lastValidText.length()) ? lastValidText.length() : cursorPos));
                        }
                    });
            } else {
                setLastValidText(box.getText());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lastValidText  the lastValidText to set
     */
    public void setLastValidText(String lastValidText) {
        if (lastValidText == null) {
            lastValidText = "";
        }
        this.lastValidText = lastValidText;
    }
}
