package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;

public class CellLabel extends Component {
    public static final int TEXT_SIZE = 7;
    public static final int TILE_SIZE = DungeonTilemap.SIZE;
    public static final int GAP = 1;

    RenderedTextBlock labelText;
    Image labelImage;

    CellLabel() {
        this("");
    }

    public CellLabel(String text) {
        this(text, Icons.CELL_LABEL.get());
    }

    CellLabel(String text, Image image) {
        super();
        labelText.text(text);
        labelImage.copy(image);
    }

    @Override
    protected void createChildren() {
        labelText = GameScene.renderTextBlock(TEXT_SIZE);
        labelText.align(RenderedTextBlock.CENTER_ALIGN);
        add(labelText);

        labelImage = new Image();
        add(labelImage);
    }

    @Override
    protected void layout() {
        labelImage.scale = new PointF(width()/labelImage.width(), height()/labelImage.height());

        labelImage.center(new PointF(this.x, this.y));
        labelImage.visible = true;

        labelText.setPos(this.x - labelText.width()/2,
                this.y - labelImage.height()/2 - labelText.height() - GAP);
        labelText.visible = true;

    }

    public Image image() {
        return labelImage;
    }

    public void image(Image image) {
        labelImage.copy(image);
    }

    public RenderedTextBlock text() {
        return labelText;
    }

    public void text(String text) {
        labelText.text(text);
    }

    public void hardlight(int color) {
        labelText.hardlight(color);
        labelImage.hardlight(color);
    }
}
