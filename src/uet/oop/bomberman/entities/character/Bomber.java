package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;

import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    /**
     * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo,
     * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
     */
    protected int _timeBetweenPutBombs = 0;

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    /**
     * Kiểm tra xem có đặt được bom hay không? nếu có thì đặt bom tại vị trí hiện tại của Bomber
     */
    private void detectPlaceBomb() {
        // TODO: kiểm tra xem phím điều khiển đặt bom có được gõ và giá trị _timeBetweenPutBombs, Game.getBombRate() có thỏa mãn hay không
        if(_input.space && Game.getBombRate()>0 && _timeBetweenPutBombs < 0){
            int x = Coordinates.pixelToTile(_x + _sprite.getSize()/2); // put bom o vi tri pos x:y
            int y = Coordinates.pixelToTile(_y - _sprite.getSize()/2 );

            placeBomb(x,y);
            Game.addBombRate(-1);//giam so bom xuog 1
            _timeBetweenPutBombs = 30; // dat lai thoi gian co the dat bom
        }
        // TODO:  Game.getBombRate() sẽ trả về số lượng bom có thể đặt liên tiếp tại thời điểm hiện tại
        // TODO: _timeBetweenPutBombs dùng để ngăn chặn Bomber đặt 2 Bomb cùng tại 1 vị trí trong 1 khoảng thời gian quá ngắn
        // TODO: nếu 3 điều kiện trên thỏa mãn thì thực hiện đặt bom bằng placeBomb()
        // TODO: sau khi đặt, nhớ giảm số lượng Bomb Rate và reset _timeBetweenPutBombs về 0
    }

    protected void placeBomb(int x, int y) {
        // TODO: thực hiện tạo đối tượng bom, đặt vào vị trí (x, y)
        Bomb bomb = new Bomb(x,y,_board);
        _board.addBomb(bomb);

    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }

    @Override
    public void kill() {
        if (!_alive) return;
        _alive = false;
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            _board.endGame();
        }
    }

    @Override
    protected void calculateMove() {
        // TODO: xử lý nhận tín hiệu điều khiển hướng đi từ _input và gọi move() để thực hiện di chuyển
        double xa = 0 , ya = 0 ;
        //TODO: ban dau cho toa do bang 0, bat su kien ban phim neu co thi tang toa do len 1
        if(_input.up) ya--;
        if(_input.down) ya++;
        if(_input.left) xa--;
        if(_input.right) xa++;

        // TODO: nhớ cập nhật lại giá trị cờ _moving khi thay đổi trạng thái di chuyển
        if(xa != 0 || ya != 0)  {
            move(xa * Game.getBomberSpeed(), ya * Game.getBomberSpeed());
            _moving = true;
        } else {
            _moving = false;
        }
        //TODO: dung co moving de xac dinh nhan vat co di chuyen hay khong ==> tao chuyen dong
    }
    @Override
    public boolean canMove(double x, double y) {
        // TODO: kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không
        System.out.println(_y + "::" + _x);
        System.out.println(y + "::" + x);
        for(int c = 0; c < 4 ; c++){
            double xt = ((_x + x) + c % 2 * 11) / Game.TILES_SIZE;
            // TODO: c la huong nv, bieu thuc lau diem de xac dinh no co va cham khong
            // TODO: chia cho Tile_Size de lay ra vi tri trong ma tran Map
            double yt = ((_y + y) + c / 2 * 12 - 13) / Game.TILES_SIZE; //TODO: bieu thuc da duoc test, co tham khao code mau


            Entity a = _board.getEntity(xt,yt,this);
            if(!a.collide(this))//TODO: neu va cham thi se chay chuoi lenh trng if
                return false;//TODO: khong the di chuyen
        }
        return true;//co the di chuyen
    }

    @Override
    public void move(double xa, double ya) {

        // TODO: sử dụng canMove() để kiểm tra xem có thể di chuyển tới điểm đã tính toán hay không và thực hiện thay đổi tọa độ _x, _y
        if(canMove(xa, ya)) {
            _y += ya;
            _x += xa;
        }


        // TODO: nhớ cập nhật giá trị _direction sau khi di chuyển
        if(xa > 0) _direction = 1;
        if(xa < 0) _direction = 3;
        if(ya > 0) _direction = 2;
        if(ya < 0) _direction = 0;
    }

    @Override
    public boolean collide(Entity e) {
        // TODO: xử lý va chạm với Flame
        if( e instanceof Flame){
            kill();//nhan vat chet
            return false;
        }
        // TODO: xử lý va chạm với Enemy
        if( e instanceof Enemy){
            kill();
            return true;// ballon co the di qua bomber
        }
        return true;
    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }
}
