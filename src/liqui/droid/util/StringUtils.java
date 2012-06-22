
package liqui.droid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * The Class StringUtils.
 */
public class StringUtils {

    /** The Constant DATE_FORMAT. */
    private static final String DATE_FORMAT = "MMM dd, yyyy";

    /** The Constant MEGABYTE. */
    private static final long MEGABYTE = 1024L;

    /** The Constant EMAIL_ADDRESS_PATTERN. */
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    /**
     * Checks if is blank.
     * 
     * @param val the val
     * @return true, if is blank
     */
    public static boolean isBlank(String val) {
        return val == null || (val != null && val.trim().equals(""));
    }

    /**
     * Format long text dot.
     * 
     * @param text the text
     * @return the string
     */
    public static String formatLongTextDot(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        } else {
            int index = text.indexOf(".");
            if (index != -1) {
                return text.substring(0, index);
            } else {
                return StringUtils.formatLongText(text);
            }
        }
    }

    /**
     * Format long text.
     * 
     * @param text the text
     * @return the string
     */
    public static String formatLongText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        } else {
            if (text.length() <= 20) {
                return text;
            } else if (text.length() > 20) {
                int newLineIndex = text.indexOf("\n");
                if (newLineIndex != -1) {
                    return text.substring(0, newLineIndex);
                } else {
                    return text.substring(0, text.length() > 20 ? 20 : text.length()) + "...";
                }
            } else {
                return text.substring(0, text.length() > 20 ? 20 : text.length()) + "...";
            }
        }
    }

    /**
     * Format long text.
     * 
     * @param text the text
     * @param length the length
     * @return the string
     */
    public static String formatLongText(String text, int length) {
        if (StringUtils.isBlank(text)) {
            return "";
        } else {
            if (text.length() <= length) {
                return text;
            } else {
                return text.substring(0, length);
            }
        }
    }

    /**
     * Do teaser.
     * 
     * @param text the text
     * @return the string
     */
    public static String doTeaser(String text) {
        if (!StringUtils.isBlank(text)) {
            int indexNewLine = text.indexOf("\n");
            int indexDot = text.indexOf(". ");

            if (indexDot != -1 && indexNewLine != -1) {
                if (indexDot > indexNewLine) {
                    text = text.substring(0, indexNewLine);
                } else {
                    text = text.substring(0, indexDot + 1);
                }
            } else if (indexDot != -1) {
                text = text.substring(0, indexDot + 1);
            } else if (indexNewLine != -1) {
                text = text.substring(0, indexNewLine);
            }

            return text;
        }
        return "";
    }

    /**
     * Repeat.
     * 
     * @param val the val
     * @param count the count
     * @return the string
     */
    public static String repeat(String val, int count) {
        String val1 = "";
        for (int i = 0; i < count; i++) {
            val1 += val;
        }
        return val1;
    }

    /**
     * Kbytes to meg.
     * 
     * @param kbytes the kbytes
     * @return the float
     */
    public static float kbytesToMeg(long kbytes) {
        return kbytes / MEGABYTE;
    }

    /**
     * Md5 hex.
     * 
     * @param s the s
     * @return the string
     */
    public static String md5Hex(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes());
            result = toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // this won't happen, we know Java has MD5!
        }
        return result;
    }

    /**
     * To hex.
     * 
     * @param a the a
     * @return the string
     */
    public static String toHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (int i = 0; i < a.length; i++) {
            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(a[i] & 0x0f, 16));
        }
        return sb.toString();
    }

    /**
     * If null default to.
     * 
     * @param ori the ori
     * @param defaultTo the default to
     * @return the string
     */
    public static String ifNullDefaultTo(String ori, String defaultTo) {
        if (!StringUtils.isBlank(ori)) {
            return ori;
        } else {
            if (!StringUtils.isBlank(defaultTo)) {
                return defaultTo;
            } else {
                return "";
            }
        }
    }

    /**
     * Format name.
     * 
     * @param userLogin the user login
     * @param name the name
     * @return the string
     */
    public static String formatName(String userLogin, String name) {
        String formattedName;

        if (!StringUtils.isBlank(name)) {
            formattedName = userLogin + " - " + name;
        } else {
            formattedName = userLogin;
        }
        return formattedName;
    }

    /**
     * Convert stream to string.
     * 
     * @param is the is
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the Reader.read(char[]
         * buffer) method. We iterate until the Reader return -1 which means
         * there's no more data to read. We use the StringWriter class to
         * produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    /**
     * Format date.
     * 
     * @param date the date
     * @return the string
     */
    public static String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.DATE_FORMAT);
            return sdf.format(date);
        } else {
            return "";
        }
    }

    /**
     * Gets the file extension.
     * 
     * @param filename the filename
     * @return the file extension
     */
    public static String getFileExtension(String filename) {
        int mid = filename.lastIndexOf(".");
        if (mid != -1) {
            return filename.substring(mid + 1, filename.length());
        } else {
            return "";
        }
    }

    /**
     * To human readble format.
     *
     * @param size the size
     * @return the string
     */
    public static String toHumanReadbleFormat(long size) {
        NumberFormat nf = new DecimalFormat("0.#");
        String s;
        float f;
        if (size / (1024 * 1024) > 0) {
            f = size;
            s = nf.format(f / (1024 * 1024)) + " GB";
        } else if (size / (1024) > 0) {
            f = size;
            s = nf.format(f / (1024)) + " MB";
        } else {
            f = size;
            s = nf.format(f) + " bytes";
        }
        return s;
    }

    /**
     * Encode url.
     *
     * @param s the s
     * @return the string
     */
    @SuppressWarnings("deprecation")
    public static String encodeUrl(String s) {
        // s = s.replaceAll("%", "%25");
        // s = s.replaceAll(" ", "%20");
        // s = s.replaceAll("!", "%21");
        // s = s.replaceAll("\"", "%22");
        // s = s.replaceAll("#", "%23");
        s = URLEncoder.encode(s).replaceAll("\\+", "%20");
        return s;
    }

    /**
     * Check email.
     *
     * @param email the email
     * @return true, if successful
     */
    public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    
    /**
     * Returns the MD5 of the given string.
     * @param s the string to convert.
     * @return the MD5.
     */
    public static String md5(String s) {
        
        if (s == null) {
            return null;
        }
        
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static String join(String[] strings, String j) {
        String s = "";
        
        for (int i = 0; i < strings.length; i++) {
            s += strings[i];
            
            if (i < strings.length - 1) {
                s += j;
            }
        }
        
        return s;
    }

}
