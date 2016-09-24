package game;

import truetypefont.TrueTypeFont;
import truetypefont.TrueTypeKey;
import truetypefont.TrueTypeLoader;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class MyGame extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		// ע��ttf������Դ������
		assetManager.registerLoader(TrueTypeLoader.class, "ttf");

		// �������� (���磺����)
		TrueTypeKey ttk = new TrueTypeKey("Interface/Fonts/SIMKAI.TTF",// ����
				java.awt.Font.PLAIN,// ���Σ���ͨ��б�塢����
				64);// �ֺ�
		
		TrueTypeFont font = (TrueTypeFont)assetManager.loadAsset(ttk);

		String[] poem = {"��ɽ�����", "����������", "�����ɼ���", "��Ȫʯ����"};
		
		for(int i=0; i<poem.length; i++) {
			// ��������
			Geometry text = font.getBitmapGeom(poem[i], 0, ColorRGBA.White);
			text.scale(0.5f);
			text.setLocalTranslation(450, 450 - i*32, 0);
			guiNode.attachChild(text);
		}
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

}
