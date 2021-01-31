package com.alexlabbane.blocksolver;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;

public class Solution {
	private ArrayList<ArrayList<ArrayList<Character>>> cube;
	
	public static char emp = '.';
	public static ArrayList<Material> pieceMaterials;
	
	public Solution(ArrayList<ArrayList<ArrayList<Character>>> inputCube) {
		this.cube = inputCube;
	}
	
	public Solution() {
		this.cube = null;
	}
	
	public static void initializePieceMaterials() {
		pieceMaterials = new ArrayList<Material>();
		pieceMaterials.add(Material.RED_CONCRETE);
		pieceMaterials.add(Material.GREEN_CONCRETE);
		pieceMaterials.add(Material.BLUE_CONCRETE);
		pieceMaterials.add(Material.BLACK_CONCRETE);
		pieceMaterials.add(Material.GRAY_CONCRETE);
		pieceMaterials.add(Material.YELLOW_CONCRETE);
		pieceMaterials.add(Material.MAGENTA_CONCRETE);
	}
	
	public void setCube(ArrayList<ArrayList<ArrayList<Character>>> inputCube) {
		this.cube = inputCube;
	}
	
	public void showSolution(Location loc) {
		for(int i = 1; i < BlockSolver.CUBE_SIZE + 1; i++) {
			for(int j = 1; j < BlockSolver.CUBE_SIZE + 1; j++) {
				for(int k = 1; k < BlockSolver.CUBE_SIZE + 1; k++) {
					Character block = this.cube.get(i-1).get(j-1).get(k-1);
					Location l = new Location(loc.getWorld(), loc.getX() + i, loc.getY() + k, loc.getZ() + j);
					switch(block) {
					case '0':
						l.getBlock().setType(pieceMaterials.get(0));
						break;
					case '1':
						l.getBlock().setType(pieceMaterials.get(1));
						break;
					case '2':
						l.getBlock().setType(pieceMaterials.get(2));
						break;
					case '3':
						l.getBlock().setType(pieceMaterials.get(3));
						break;
					case '4':
						l.getBlock().setType(pieceMaterials.get(4));
						break;
					case '5':
						l.getBlock().setType(pieceMaterials.get(5));
						break;
					case '6':
						l.getBlock().setType(pieceMaterials.get(6));
						break;
					default:
						l.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}
	
	
}
