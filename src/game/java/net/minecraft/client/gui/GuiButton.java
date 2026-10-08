package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class GuiButton extends Gui {
	protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
	private static final int BUTTON_BORDER = 0xFF713900;
	private static final int BUTTON_ORANGE = 0xFFF28C00;
	private static final int BUTTON_YELLOW = 0xFFFFD21F;
	private static final int BUTTON_DISABLED = 0xFFD98200;
	private static final int BUTTON_TEXT = 0xFF2B1700;

	/** Button width in pixels */
	public int width;

	/** Button height in pixels */
	protected int height;

	/** The x position of this control. */
	public int xPosition;

	/** The y position of this control. */
	public int yPosition;

	/** The string displayed on this control. */
	public String displayString;
	public int id;

	/** True if this control is enabled, false to disable. */
	public boolean enabled;

	/** Hides the button completely if false. */
	public boolean visible;
	protected boolean hovered;

	public GuiButton(int buttonId, int x, int y, String buttonText) {
		this(buttonId, x, y, 200, 20, buttonText);
	}

	public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		this.width = 200;
		this.height = 20;
		this.enabled = true;
		this.visible = true;
		this.id = buttonId;
		this.xPosition = x;
		this.yPosition = y;
		this.width = widthIn;
		this.height = heightIn;
		this.displayString = buttonText;
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this
	 * button and 2 if it IS hovering over this button.
	 */
	protected int getHoverState(boolean mouseOver) {
		int i = 1;

		if (!this.enabled) {
			i = 0;
		} else if (mouseOver) {
			i = 2;
		}

		return i;
	}

	protected void drawThemedBackground(boolean buttonEnabled, boolean buttonHovered) {
		int background = !buttonEnabled ? BUTTON_DISABLED : buttonHovered ? BUTTON_YELLOW : BUTTON_ORANGE;
		this.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height,
				BUTTON_BORDER);
		this.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1,
				this.yPosition + this.height - 1, background);
		if (this.width > 2 && this.height > 2) {
			this.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1,
					this.yPosition + 2, buttonEnabled && buttonHovered ? 0xFFFFE98A : 0xFFFFB52E);
		}
	}

	protected int getThemedTextColor() {
		return BUTTON_TEXT;
	}

	protected void setThemedTextureColor(boolean buttonEnabled, boolean buttonHovered) {
		int color = !buttonEnabled ? BUTTON_DISABLED : buttonHovered ? BUTTON_YELLOW : BUTTON_ORANGE;
		GlStateManager.color(((color >> 16) & 255) / 255.0F, ((color >> 8) & 255) / 255.0F,
				(color & 255) / 255.0F, 1.0F);
	}

	protected void drawThemedSliderHandle(int x) {
		this.drawRect(x - 1, this.yPosition + 2, x + 5, this.yPosition + this.height - 2, BUTTON_BORDER);
		this.drawRect(x, this.yPosition + 3, x + 4, this.yPosition + this.height - 3, BUTTON_YELLOW);
	}

	public void func_191745_a(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_) {
		if (this.visible) {
			FontRenderer fontrenderer = p_191745_1_.fontRendererObj;
			this.hovered = p_191745_2_ >= this.xPosition && p_191745_3_ >= this.yPosition
					&& p_191745_2_ < this.xPosition + this.width && p_191745_3_ < this.yPosition + this.height;
			if (this.hovered && this.enabled) {
				Mouse.showCursor(EnumCursorType.HAND);
			}
			this.drawThemedBackground(this.enabled, this.hovered);
			this.mouseDragged(p_191745_1_, p_191745_2_, p_191745_3_);
			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,
					this.yPosition + (this.height - 8) / 2, this.getThemedTextColor());
		}
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int mouseX, int mouseY) {
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition
				&& mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	/**
	 * Whether the mouse cursor is currently over the button.
	 */
	public boolean isMouseOver() {
		return this.hovered;
	}

	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
	}

	public void playPressSound(SoundHandler soundHandlerIn) {
		soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public int getButtonWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
