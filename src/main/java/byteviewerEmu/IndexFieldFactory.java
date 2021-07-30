/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package byteviewerEmu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.math.BigInteger;

import docking.widgets.fieldpanel.field.Field;
import docking.widgets.fieldpanel.field.SimpleTextField;
import docking.widgets.fieldpanel.support.Highlight;
import docking.widgets.fieldpanel.support.HighlightFactory;
import ghidra.app.plugin.core.format.ByteBlockInfo;

/**
 * Implementation for the index/address field.
 */
class IndexFieldFactory {

	private FontMetrics fm;
	private int width;
	private IndexMap indexMap;
	private int charWidth;
	private String noValueStr;
	private int startX;
	private Color missingValueColor;
	private HighlightFactory highlightFactory = new DummyHighlightFactory();

	/**
	 * Constructor
	 * @param fieldModel field model
	 */
	IndexFieldFactory(FontMetrics fm) {
		this.fm = fm;

		charWidth = fm.charWidth('W');
		width = ByteViewerComponentProviderEmu.DEFAULT_NUMBER_OF_CHARS * charWidth;
		missingValueColor = ByteViewerComponentProviderEmu.DEFAULT_MISSING_VALUE_COLOR;
	}

	/**
	 * Gets a Field object for the given index.
	 * This method is called for n times where n is the number of fields
	 * in the model. In this case, we only have one field, so it gets
	 * called once for each index.
	 */
	public Field getField(BigInteger index) {

		if (indexMap == null) {
			return null;
		}

		// translate index to block and offset into the block
		ByteBlockInfo info = indexMap.getBlockInfo(index, 0);
		if (info == null) {
			for (int i = 0; i < indexMap.getBytesPerLine(); i++) {
				info = indexMap.getBlockInfo(index, i);
				if (info != null) {
					break;
				}
			}
			if (info == null) {
				if (indexMap.showSeparator(index)) {
					SimpleTextField sf =
						new SimpleTextField(noValueStr, fm, startX, width, false, highlightFactory);

					sf.setForeground(missingValueColor);
					return sf;
				}
				return null;
			}
		}
		String locRep = info.getBlock().getLocationRepresentation(info.getOffset());
		if (locRep == null) {
			return null;
		}
		return new SimpleTextField(locRep, fm, startX, width, false, highlightFactory);
	}

	public FontMetrics getMetrics() {
		return fm;
	}

	/**
	 * Sets the starting x position for the fields generated by this factory
	 */
	public void setStartX(int x) {
		startX = x;
	}

	/**
	 * Returns the starting x position for the fields generated by this factory.
	 */
	public int getStartX() {
		return startX;
	}

	/**
	 * Returns the width of the fields associated with this Factory.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Set byte blocks.
	 * @param blocks
	 */
	void setIndexMap(IndexMap indexMap, int maxWidth) {
		this.indexMap = indexMap;
		width = maxWidth - (2 * charWidth);
		int nchars = width / charWidth;
		if (indexMap != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nchars; i++) {
				sb.append(".");
			}
			noValueStr = sb.toString();
		}
	}

	void setMissingValueColor(Color c) {
		missingValueColor = c;
	}

	static class DummyHighlightFactory implements HighlightFactory {
		private final Highlight[] NO_HIGHLIGHTS = new Highlight[0];

		public DummyHighlightFactory() {
		}

		@Override
		public Highlight[] getHighlights(Field field, String text, int cursorTextOffset) {
			return NO_HIGHLIGHTS;
		}
	}

}
