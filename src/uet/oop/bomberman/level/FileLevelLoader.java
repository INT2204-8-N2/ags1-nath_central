package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Balloon;
import uet.oop.bomberman.entities.character.enemy.Oneal;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.SpeedItem;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

import java.io.*;
import java.net.URL;
import java.util.StringTokenizer;

public class FileLevelLoader extends LevelLoader {

	/**
	 * Ma trận chứa thông tin bản đồ, mỗi phần tử lưu giá trị kí tự đọc được
	 * từ ma trận bản đồ trong tệp cấu hình
	 */
	private static char[][] _map;
	
	public FileLevelLoader(Board board, int level) throws LoadLevelException {
		super(board, level);
	}
	
	@Override
	public void loadLevel(int level) throws LoadLevelException {
		// TODO: đọc dữ liệu từ tệp cấu hình /levels/Level{level}.txt
        try {
			URL absPath = FileLevelLoader.class.getResource("/levels/Level" + level  + ".txt");

            BufferedReader in = new BufferedReader(new InputStreamReader(absPath.openStream()));// doc bang bo nho dem BUFFRTREADER

            String data = in.readLine(); // Lay ra dong dau tien
            StringTokenizer tokens = new StringTokenizer(data); // lay cac ky tu sau dau cach

			// TODO: cập nhật các giá trị đọc được vào _width, _height, _level, _map
            _level = Integer.parseInt(tokens.nextToken());
            _height = Integer.parseInt(tokens.nextToken());
            _width = Integer.parseInt(tokens.nextToken());
            //TODO: doc map
            _map = new char[_height][_width];
			for(int i = 0; i < _height; i++) {
				String s = in.readLine();
				int j=0;
				while (j<_width) {
					int index = s.charAt(j);
					_map[i][j] = (char) index;
					j++;
				}
			}
			for(int i=0;i<_height;i++){
				for (int j=0;j<_width;j++){
					System.out.print(_map[i][j]);
				}
				System.out.println();
			}
			in.close();
        } catch (IOException e) {
            throw new LoadLevelException("Error loading level " + level, e);
        }

	}

	@Override
	public void createEntities() {
		// TODO: tạo các Entity của màn chơi
		// TODO: sau khi tạo xong, gọi _board.addEntity() để thêm Entity vào game
        // TODO: chu y doi tuog di chuyen ta de vi tri dang Pixel con doi tuong tinh ta de la Tile
        // TODO: pos la vi tri cua doi tuong tren ma tran Board
		for(int y=0; y<_height;y++){
			for(int x=0;x<_width;x++){
				switch (_map[y][x]){
					case '#':
						_board.addEntity(x + y * _width, new Wall(x, y, Sprite.wall)); //TODO: add cac doi tuong vao board. vi tri pos TILE
						break;
					case 'p' :
						int xBomber = x, yBomber = y;
						_board.addCharacter( new Bomber(Coordinates.tileToPixel(xBomber), Coordinates.tileToPixel(yBomber) + Game.TILES_SIZE, _board) );
						Screen.setOffset(0, 0);//TODO: lop Coordinate dung de cover Tile to Pixel hoac nguoc lai doi so truyen vao la toa do tuong ung
						_board.addEntity(xBomber + yBomber * _width, new Grass(xBomber, yBomber, Sprite.grass));
						break;
					case '1' :
						int xE = x, yE = y;
						_board.addCharacter( new Balloon(Coordinates.tileToPixel(xE), Coordinates.tileToPixel(yE) + Game.TILES_SIZE, _board));
						_board.addEntity(xE + yE * _width, new Grass(xE, yE, Sprite.grass));
						break;
					case '2' :
						int xE2= x, yE2 = y;
						_board.addCharacter( new Oneal(Coordinates.tileToPixel(xE2), Coordinates.tileToPixel(yE2) + Game.TILES_SIZE, _board));
						_board.addEntity(xE2 + yE2 * _width, new Grass(xE2, yE2, Sprite.grass));
						break;
					case '*' :
						int xB = x, yB = y;
						_board.addEntity(xB + yB * _width,
								new LayeredEntity(xB, yB,
										new Grass(xB, yB, Sprite.grass),
										new Brick(xB, yB, Sprite.brick)
								)
						);
						//TODO: layeredEntity la cac entity chong nhau va hien thi cai o tren cung
						break;
					case 'f' :
						int xF = x, yF = y;
						_board.addEntity(xF + yF * _width,
								new LayeredEntity(xF, yF,
										new Grass(xF ,yF, Sprite.grass),
										new FlameItem(xF, yF, Sprite.powerup_flames),
										new Brick(xF, yF, Sprite.brick)
								)
						);
						break;
					case 'b' :
						int xI = x, yI = y;
						_board.addEntity(xI + yI * _width,
								new LayeredEntity(xI, yI,
										new Grass(xI ,yI, Sprite.grass),
										new BombItem(xI, yI, Sprite.powerup_bombs),
										new Brick(xI, yI, Sprite.brick)
								)
						);
						break;
					case 's' :
						int xS = x, yS = y;
						_board.addEntity(xS + yS * _width,
								new LayeredEntity(xS, yS,
										new Grass(xS ,yS, Sprite.grass),
										new SpeedItem(xS, yS, Sprite.powerup_speed),
										new Brick(xS, yS, Sprite.brick)
								)
						);
						break;
					case 'x' :
						int xX = x, yX = y;
						_board.addEntity(xX + yX * _width,
								new LayeredEntity(xX, yX,
										new Grass(xX ,yX, Sprite.grass),
										new Portal(xX, yX, Sprite.portal),
										new Brick(xX, yX, Sprite.brick)
								)
						);
						break;
						default :
							int xA = x, yA = y;
							_board.addEntity(xA + yA * _width,
									new LayeredEntity(xA, yA,
											new Grass(xA ,yA, Sprite.grass)
									)
							);
							break;

				}
			}
		}
		// TODO: phần code mẫu ở dưới để hướng dẫn cách thêm các loại Entity vào game
		// TODO: hãy xóa nó khi hoàn thành chức năng load màn chơi từ tệp cấu hình
//		// thêm Wall
//
//		for (int x = 0; x < 20; x++) {
//			for (int y = 0; y < 20; y++) {
//				int pos = x + y * _width;
//				Sprite sprite = y == 0 || x == 0 || x == 10 || y == 10 ? Sprite.wall : Sprite.grass;
//				_board.addEntity(pos, new Grass(x, y, sprite));
//			}
//		}
//
//		// thêm Bomber
//		int xBomber = 1, yBomber = 1;
//		_board.addCharacter( new Bomber(Coordinates.tileToPixel(xBomber), Coordinates.tileToPixel(yBomber) + Game.TILES_SIZE, _board) );
//		Screen.setOffset(0, 0);
//		_board.addEntity(xBomber + yBomber * _width, new Grass(xBomber, yBomber, Sprite.grass));
//
//		// thêm Enemy
//		int xE = 2, yE = 1;
//		_board.addCharacter( new Balloon(Coordinates.tileToPixel(xE), Coordinates.tileToPixel(yE) + Game.TILES_SIZE, _board));
//		_board.addEntity(xE + yE * _width, new Grass(xE, yE, Sprite.grass));
//
//		// thêm Brick
//		int xB = 3, yB = 1;
//		_board.addEntity(xB + yB * _width,
//				new LayeredEntity(xB, yB,
//					new Grass(xB, yB, Sprite.grass),
//					new Brick(xB, yB, Sprite.brick)
//				)
//		);
//
//		// thêm Item kèm Brick che phủ ở trên
//		int xI = 1, yI = 2;
//		_board.addEntity(xI + yI * _width,
//				new LayeredEntity(xI, yI,
//					new Grass(xI ,yI, Sprite.grass),
//					new SpeedItem(xI, yI, Sprite.powerup_flames),
//					new Brick(xI, yI, Sprite.brick)
//				)
//		);
	}

}
