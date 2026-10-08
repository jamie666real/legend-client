package net.peyton.eagler.gui;

import java.util.ArrayList;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;

import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.lax1dude.eaglercraft.profile.SkinPreviewRenderer;

import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_DEPTH_BUFFER_BIT;

public class GuiCredits extends GuiScreen {

	private ArrayList<String> credits = new ArrayList<String>();

	private int mouseY;
	private int scrollPosition = 0;
	private int visibleLines = 1;
	private int panelX;
	private int panelY;
	private int panelWidth;
	private int panelHeight;
	private int contentTop;
	private int trackHeight;
	private String creditsText;
	private float previewMouseX;
	private float previewMouseY;
	private long previewAnimationTime = -1L;

	private int dragstart = -1;
	private int dragstartI = -1;

	String fileLocation;
	GuiScreen parentScreen;

	public GuiCredits(GuiScreen screen, String location) {
		this.fileLocation = location;
		this.parentScreen = screen;
	}

	public void initGui() {
		this.panelWidth = Math.min(430, Math.max(1, this.width - 24));
		this.panelHeight = Math.min(310, Math.max(1, this.height - 24));
		this.panelX = (this.width - this.panelWidth) / 2;
		this.panelY = (this.height - this.panelHeight) / 2;
		this.contentTop = this.panelY + 57;
		int footerTop = this.panelY + this.panelHeight - 28;
		this.trackHeight = Math.max(1, footerTop - this.contentTop - 8);
		this.visibleLines = Math.max(1, this.trackHeight / 10);

		if (this.creditsText == null) {
			this.creditsText = EagRuntime.getRequiredResourceString(this.fileLocation);
		}
		this.credits.clear();
		int textWidth = Math.max(1, this.panelWidth - (this.panelWidth >= 260 ? 150 : 54));
		for (String line : this.creditsText.split("\n", -1)) {
			String trimmedLine = line.trim();
			if (trimmedLine.isEmpty()) {
				this.credits.add("");
			} else {
				String currentLine = "   ";
				for (String word : trimmedLine.split(" ")) {
					String candidate = currentLine + word + " ";
					if (this.mc.fontRendererObj.getStringWidth(candidate) < textWidth) {
						currentLine = candidate;
					} else {
						if (!currentLine.trim().isEmpty()) {
							this.credits.add(currentLine);
						}
						currentLine = word + " ";
					}
				}
				this.credits.add(currentLine);
			}
		}
		this.scrollPosition = Math.min(this.scrollPosition, Math.max(0, this.credits.size() - this.visibleLines));
	}

	protected void actionPerformed(GuiButton button) throws IOException {
	}

	protected void mouseClicked(int par1, int par2, int par3) {
		if (par3 == 0) {
			int closeX = this.panelX + this.panelWidth - 30;
			if (par1 >= closeX && par1 <= closeX + 18 && par2 >= this.panelY + 9 && par2 <= this.panelY + 27) {
				mc.displayGuiScreen(parentScreen);
				return;
			}
			if (this.credits.size() > this.visibleLines && par1 >= this.panelX + this.panelWidth - 19
					&& par1 <= this.panelX + this.panelWidth - 11
					&& par2 >= this.contentTop + this.getThumbOffset()
					&& par2 <= this.contentTop + this.getThumbOffset() + this.getThumbHeight()) {
				dragstart = par2;
				dragstartI = scrollPosition;
			}
		}
	}

	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(0, 0, par3);
		this.drawDefaultBackground();
		this.mouseY = par2;
		int lines = this.credits.size();
		this.clampScrollPosition();

		drawRect(0, 0, this.width, this.height, 0x55000000);
		drawRect(this.panelX + 3, this.panelY + 4, this.panelX + this.panelWidth + 3,
				this.panelY + this.panelHeight + 4, 0x88000000);
		drawRect(this.panelX - 1, this.panelY - 1, this.panelX + this.panelWidth + 1,
				this.panelY + this.panelHeight + 1, 0xFF52677F);
		drawRect(this.panelX, this.panelY, this.panelX + this.panelWidth, this.panelY + this.panelHeight,
				0xF20D1520);
		drawRect(this.panelX + 1, this.panelY + 1, this.panelX + this.panelWidth - 1, this.panelY + 49,
				0xCC172435);
		drawRect(this.panelX + 1, this.panelY + 48, this.panelX + this.panelWidth - 1, this.panelY + 50,
				0xFF55C7E8);

		this.drawCenteredString(this.mc.fontRendererObj, "CREDITS", this.width / 2, this.panelY + 11, 0xFFFFFFFF);
		this.drawCenteredString(this.mc.fontRendererObj, "The people and projects behind this client",
				this.width / 2, this.panelY + 29, 0xFFB8C7D8);

		int closeX = this.panelX + this.panelWidth - 30;
		boolean closeHovered = par1 >= closeX && par1 <= closeX + 18 && par2 >= this.panelY + 9
				&& par2 <= this.panelY + 27;
		drawRect(closeX, this.panelY + 9, closeX + 19, this.panelY + 28,
				closeHovered ? 0xFFB94A55 : 0xFF334255);
		this.drawCenteredString(this.mc.fontRendererObj, "x", closeX + 9, this.panelY + 14, 0xFFFFFFFF);

		int textX = this.panelX + 17;
		int textY = this.contentTop;
		for (int i = 0; i < this.visibleLines && this.scrollPosition + i < lines; ++i) {
			this.mc.fontRendererObj.drawStringWithShadow(this.credits.get(this.scrollPosition + i), textX, textY + i * 10,
					0xFFE6EDF5);
		}

		if (this.panelWidth >= 260 && this.panelHeight >= 220) {
			int previewLeft = Math.max(this.panelX + 6, this.panelX + this.panelWidth - 122);
			int previewRight = this.panelX + this.panelWidth - 10;
			int previewX = (previewLeft + previewRight) / 2;
			int previewY = this.contentTop + 145;
			drawRect(previewLeft, this.contentTop + 4, previewRight, this.contentTop + 166, 0x88304052);
			String playerName = EaglerProfile.getName();
			if (playerName == null || playerName.trim().isEmpty()) {
				playerName = "Player";
			}
			this.drawCenteredString(this.mc.fontRendererObj, playerName, previewX, this.contentTop + 12, 0xFFE6EDF5);
			long now = EagRuntime.steadyTimeMillis();
			if (this.previewAnimationTime < 0L) {
				this.previewMouseX = par1;
				this.previewMouseY = par2;
				this.previewAnimationTime = now;
			}
			long elapsed = Math.max(0L, Math.min(100L, now - this.previewAnimationTime));
			this.previewAnimationTime = now;
			float smoothing = 1.0F - (float) Math.exp(-elapsed / 100.0D);
			long glanceCycle = now % 7800L;
			float glance = glanceCycle >= 5300L && glanceCycle <= 6700L
					? (float) Math.sin((glanceCycle - 5300L) * Math.PI / 1400.0D) : 0.0F;
			float idleSway = (float) Math.sin(now * 0.00045D) * 18.0F;
			float idleTilt = (float) Math.sin(now * 0.0008D) * 5.0F;
			this.previewMouseX += (par1 - idleSway - glance * 130.0F - this.previewMouseX) * smoothing;
			this.previewMouseY += (par2 + idleTilt - this.previewMouseY) * smoothing;
			GlStateManager.clear(GL_DEPTH_BUFFER_BIT);
			SkinPreviewRenderer.renderNpcPreview(previewX, previewY, (int) this.previewMouseX,
					(int) this.previewMouseY, EaglerProfile.getActiveSkinModel(), EaglerProfile.getActiveSkinResourceLocation(),
					EaglerProfile.getActiveCapeResourceLocation(), now);
		}

		int trackX = this.panelX + this.panelWidth - 17;
		if (lines > this.visibleLines) {
			int thumbY = this.contentTop + this.getThumbOffset();
			boolean thumbHovered = par1 >= trackX - 1 && par1 <= trackX + 8
					&& par2 >= thumbY && par2 <= thumbY + this.getThumbHeight();
			drawRect(trackX + 3, this.contentTop, trackX + 5, this.contentTop + this.trackHeight, 0xFF263445);
			drawRect(trackX, thumbY, trackX + 8, thumbY + this.getThumbHeight(),
					thumbHovered ? 0xFF55C7E8 : 0xFF8CA4BB);
		}

		int footerY = this.panelY + this.panelHeight - 27;
		drawRect(this.panelX + 1, footerY, this.panelX + this.panelWidth - 1, footerY + 1, 0xFF334255);
		this.mc.fontRendererObj.drawStringWithShadow("Scroll to explore", this.panelX + 16, footerY + 9, 0xFF9AADC1);
		String count = lines == 0 ? "0 credits" : (this.scrollPosition + 1) + "-"
				+ Math.min(lines, this.scrollPosition + this.visibleLines) + " / " + lines;
		this.mc.fontRendererObj.drawStringWithShadow(count,
				this.panelX + this.panelWidth - 16 - this.mc.fontRendererObj.getStringWidth(count), footerY + 9,
				0xFF9AADC1);
	}

	public void updateScreen() {
		if (Mouse.isButtonDown(0) && dragstart > 0) {
			int maxScroll = Math.max(0, this.credits.size() - this.visibleLines);
			int scrollRange = Math.max(1, this.trackHeight - this.getThumbHeight());
			this.scrollPosition = this.dragstartI + (this.mouseY - this.dragstart) * maxScroll / scrollRange;
			this.clampScrollPosition();
		} else {
			dragstart = -1;
		}
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int var1 = Mouse.getEventDWheel();
		if (var1 < 0) {
			scrollPosition += 3;
			this.clampScrollPosition();
		}
		if (var1 > 0) {
			scrollPosition -= 3;
			this.clampScrollPosition();
		}
	}

	private int getThumbHeight() {
		return Math.min(this.trackHeight, Math.max(12, this.trackHeight * this.visibleLines / this.credits.size()));
	}

	private int getThumbOffset() {
		int maxScroll = this.credits.size() - this.visibleLines;
		return maxScroll <= 0 ? 0
				: (this.trackHeight - this.getThumbHeight()) * this.scrollPosition / maxScroll;
	}

	private void clampScrollPosition() {
		this.scrollPosition = Math.max(0, Math.min(this.scrollPosition, this.credits.size() - this.visibleLines));
	}
}