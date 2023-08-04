package net.w3e.base.api.window.jcomponent;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.google.common.base.Strings;

public class JNumberTextField extends JTextField {
	private static final char DOT = '.';
	private static final char NEGATIVE = '-';
	private static final String BLANK = "";
	private static final int DEF_PRECISION = 2;

	public static final int NUMERIC = 2;
	public static final int DECIMAL = 3;

	public static final String FM_NUMERIC = "0123456789";
	public static final String FM_DECIMAL = FM_NUMERIC + DOT;

	private int maxLength = 0;
	private int format = NUMERIC;
	private String negativeChars = BLANK;
	private String allowedChars = null;
	private boolean allowNegative = false;
	private int precision = 0;

	protected PlainDocument numberFieldFilter;

	public JNumberTextField() {
		this(10, NUMERIC);
	}

	public JNumberTextField(int maxLen) {
		this(maxLen, NUMERIC);
	}

	public JNumberTextField(int maxLen, int format) {
		setAllowNegative(true);
		setMaxLength(maxLen);
		setFormat(format);

		numberFieldFilter = new JNumberFieldFilter();
		super.setDocument(numberFieldFilter);
	}

	public final void setMaxLength(int maxLen) {
		if (maxLen > 0) {
			maxLength = maxLen;
		} else {
			maxLength = 0;
		}
	}

	public final int getMaxLength() {
		return maxLength;
	}

	public final void setPrecision(int precision) {
		if (format == NUMERIC)
			return;

		if (precision >= 0) {
			this.precision = precision;
		}

		else
			this.precision = DEF_PRECISION;
	}

	public final int getPrecision() {
		return precision;
	}

	public final String getTextOrZero() {
		String text = this.getText();
		if (Strings.isNullOrEmpty(text)) {
			return "0";
		} else {
			return text;
		}
	}

	public final Number getNumber() {
		Number number = null;

		if (format == NUMERIC) {
			number = getInt();
		} else {
			number = getDouble();
		}

		return number;
	}

	public final void setNumber(Number value) {
		setText(String.valueOf(value));
	}

	public final int getInt() {
		return Integer.parseInt(getTextOrZero());
	}

	public final void setInt(int value) {
		setText(String.valueOf(value));
	}

	public final float getFloat() {
		return Float.parseFloat(getTextOrZero());
	}

	public final void setFloat(float value) {
		setText(String.valueOf(value));
	}

	public final double getDouble() {
		return Double.parseDouble(getTextOrZero());
	}

	public final void setDouble(double value) {
		setText(String.valueOf(value));
	}

	public final int getFormat() {
		return this.format;
	}

	public final void setFormat(int format) {
		switch (format) {
			case NUMERIC:
			default:
				this.format = NUMERIC;
				this.precision = 0;
				this.allowedChars = FM_NUMERIC;
				break;
			case DECIMAL:
				this.format = DECIMAL;
				this.precision = DEF_PRECISION;
				this.allowedChars = FM_DECIMAL;
				break;
		}
	}

	public final void setAllowNegative(boolean value) {
		allowNegative = value;

		if (value) {
			negativeChars = "" + NEGATIVE;
		} else {
			negativeChars = BLANK;
		}
	}

	public final boolean isAllowNegative() {
		return allowNegative;
	}

	public final void setDocument(Document document) {}

	private class JNumberFieldFilter extends PlainDocument {
		public JNumberFieldFilter() {
			super();
		}

		public void insertString(int offset, String str, AttributeSet attr)	throws BadLocationException {
			String text = getText(0,offset) + str + getText(offset,(getLength() - offset));

			if (str == null || text == null) {
				return;
			}

			for (int i=0; i<str.length(); i++) {
				if ((allowedChars + negativeChars).indexOf(str.charAt(i)) == -1) {
					return;
				}
			}

			int precisionLength = 0, dotLength = 0, minusLength = 0;
			int textLength = text.length();

			try {
				if (format == NUMERIC) {
					if (! ((text.equals(negativeChars)) && (text.length() == 1))) {
						Long.parseLong(text);
					}
				} else if (format == DECIMAL) {
					if (! ((text.equals(negativeChars)) && (text.length() == 1))) {
						Double.parseDouble(text);
					}

					int dotIndex = text.indexOf(DOT);
					if (dotIndex != -1) {
						dotLength = 1;
						precisionLength = textLength - dotIndex - dotLength;

						if (precisionLength > precision) {
							return;
						}
					}
				}
			} catch(Exception ex) {
				return;
			}

			if (text.startsWith("" + NEGATIVE)) {
				if (!allowNegative) {
					return;
				} else
					minusLength = 1;
			}

			if (maxLength < (textLength - dotLength - precisionLength - minusLength)) {
				return;
			}

			super.insertString(offset, str, attr);
		}
	}
}