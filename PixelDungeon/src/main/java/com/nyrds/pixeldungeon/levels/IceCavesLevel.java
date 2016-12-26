package com.nyrds.pixeldungeon.levels;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.mobs.npc.CagedKobold;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Scene;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Patch;
import com.watabou.pixeldungeon.levels.RegularLevel;
import com.watabou.pixeldungeon.levels.Room;
import com.watabou.pixeldungeon.levels.Room.Type;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.levels.painters.Painter;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class IceCavesLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;

		viewDistance = 6;
	}

	@Override
	public String tilesTexEx() {
		return Assets.TILES_ICE_CAVES_X;
	}


	@Override
	public String waterTex() {
		return Assets.WATER_ICE_CAVES;
	}

	protected boolean[] water() {
		return Patch.generate(this, getFeeling() == Feeling.WATER ? 0.60f : 0.45f, 6 );
	}

	protected boolean[] grass() {
		return Patch.generate(this, getFeeling() == Feeling.GRASS ? 0.55f : 0.35f, 3 );
	}

	@Override
	protected void decorate() {

		for (Room room : rooms) {
			if (room.type != Type.STANDARD) {
				continue;
			}

			if (room.width() <= 3 || room.height() <= 3) {
				continue;
			}

			int s = room.square();

			if (Random.Int( s ) > 8) {
				int corner = (room.left + 1) + (room.top + 1) * getWidth();
				if (map[corner - 1] == Terrain.WALL && map[corner - getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			if (Random.Int( s ) > 8) {
				int corner = (room.right - 1) + (room.top + 1) * getWidth();
				if (map[corner + 1] == Terrain.WALL && map[corner - getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			if (Random.Int( s ) > 8) {
				int corner = (room.left + 1) + (room.bottom - 1) * getWidth();
				if (map[corner - 1] == Terrain.WALL && map[corner + getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			if (Random.Int( s ) > 8) {
				int corner = (room.right - 1) + (room.bottom - 1) * getWidth();
				if (map[corner + 1] == Terrain.WALL && map[corner + getWidth()] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
				}
			}

			for (Room n : room.connected.keySet()) {
				if ((n.type == Type.STANDARD || n.type == Type.TUNNEL) && Random.Int( 3 ) == 0) {
					Painter.set( this, room.connected.get( n ), Terrain.EMPTY_DECO );
				}
			}
		}
		
		for (int i=getWidth() + 1; i < getLength() - getWidth(); i++) {
			if (map[i] == Terrain.EMPTY) {
				int n = 0;
				if (map[i+1] == Terrain.WALL) {
					n++;
				}
				if (map[i-1] == Terrain.WALL) {
					n++;
				}
				if (map[i+getWidth()] == Terrain.WALL) {
					n++;
				}
				if (map[i-getWidth()] == Terrain.WALL) {
					n++;
				}
				if (Random.Int( 6 ) <= n) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
		
		for (int i=0; i < getLength(); i++) {
			if (map[i] == Terrain.WALL && Random.Int( 12 ) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (Room r : rooms) {
			if (r.type == Type.STANDARD) {
				for (Room n : r.neighbours) {
					if (n.type == Type.STANDARD && !r.connected.containsKey( n )/* && Random.Int( 2 ) == 0*/) {
						Rect w = r.intersect( n );
						if (w.left == w.right && w.bottom - w.top >= 5) {
							
							w.top += 2;
							w.bottom -= 1;
							
							w.right++;
							
							Painter.fill( this, w.left, w.top, 1, w.height(), Terrain.CHASM );
							
						} else if (w.top == w.bottom && w.right - w.left >= 5) {
							
							w.left += 2;
							w.right -= 1;
							
							w.bottom++;
							
							Painter.fill( this, w.left, w.top, w.width(), 1, Terrain.CHASM );
						}
					}
				}
			}
		}
	}


	@Override
	protected void createMobs() {
		super.createMobs();

		if(Dungeon.depth==18) {
			CagedKobold.spawn(this, exitRoom(0));
		}
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case Terrain.GRASS:
			return Game.getVar(R.string.IceCaves_TileGrass);
		case Terrain.HIGH_GRASS:
			return Game.getVar(R.string.IceCaves_TileHighGrass);
		case Terrain.WATER:
			return Game.getVar(R.string.Caves_TileWater);
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return Game.getVar(R.string.IceCaves_TileStatue);
		default:
			return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
		case Terrain.ENTRANCE:
			return Game.getVar(R.string.Caves_TileDescEntrance);
		case Terrain.EXIT:
			return Game.getVar(R.string.Caves_TileDescExit);
		case Terrain.HIGH_GRASS:
			return Game.getVar(R.string.IceCaves_TileDescHighGrass);
		case Terrain.WALL_DECO:
			return Game.getVar(R.string.IceCaves_TileDescDeco);
		case Terrain.BOOKSHELF:
			return Game.getVar(R.string.Caves_TileDescBookshelf);
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return Game.getVar(R.string.IceCaves_TileDescStatue);
		default:
			return super.tileDesc( tile );
		}
	}
	
	@Override
	public void addVisuals( Scene scene ) {
		super.addVisuals( scene );
		addVisuals( this, scene );
	}
	
	public static void addVisuals( Level level, Scene scene ) {
		for (int i=0; i < level.getLength(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				scene.add( new Vein( i ) );
			}
		}
	}
	
	private static class Vein extends Group {
		
		private int pos;
		
		private float delay;
		
		public Vein( int pos ) {
			super();
			
			this.pos = pos;
			
			delay = Random.Float( 2 );
		}
		
		@Override
		public void update() {
			
			if (setVisible(Dungeon.visible[pos])) {
				
				super.update();
				
				if ((delay -= Game.elapsed) <= 0) {
					
					delay = Random.Float();
					
					PointF p = DungeonTilemap.tileToWorld( pos );
					((Sparkle)recycle( Sparkle.class )).reset( 
						p.x + Random.Float( DungeonTilemap.SIZE ), 
						p.y + Random.Float( DungeonTilemap.SIZE ) );
				}
			}
		}
	}
	
	public static final class Sparkle extends PixelParticle {
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 0.5f;
		}
		
		@Override
		public void update() {
			super.update();
			
			float p = left / lifespan;
			size( (am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2 );
		}
	}
}