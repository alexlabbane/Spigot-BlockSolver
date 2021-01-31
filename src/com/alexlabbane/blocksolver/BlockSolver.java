package com.alexlabbane.blocksolver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockSolver extends JavaPlugin implements Listener {
	
	static ArrayList<Location> pieceLocations;
	static Location goalLocation;
	static Location solutionLocation;
	static final int CUBE_SIZE = 8;
	static int nextSolutionNumber = -1;
	static ArrayList<Solution> solutions;
	
    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	getServer().getLogger().log(Level.WARNING, "Block Solver is enabled!");
    	pieceLocations = new ArrayList<Location>();
    	goalLocation = null;
    	solutionLocation = null;
    	Solution.initializePieceMaterials();
    	for(int i = 0; i < 7; i++)
    		pieceLocations.add(null);
    	
		/*
		 * getServer().getPluginManager().registerEvents(this, this);
		 * getServer().getPluginManager().registerEvents(new WaterListener(this), this);
		 * getServer().getPluginManager().registerEvents(testShop, this);
		 */
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Location commandLocation = null;
    	
    	// Get location where player executed the command
    	if(sender instanceof Player) {
    		commandLocation = ((Player) sender).getLocation();
    	} else if(sender instanceof BlockCommandSender) {
    		commandLocation = ((BlockCommandSender) sender).getBlock().getLocation();
    	}
    	
    	if(label.equals("blocksolver")) {
    		if(commandLocation == null ) {
    			Bukkit.broadcastMessage(ChatColor.RED + "Only players and command blocks can use /blocksolver");
    			return false;
    		}
    		
    		if(args[0].equals("setGoal")) {
    			Bukkit.broadcastMessage("Ran setGoal.");
        		// Sets area as goal pattern for block solver
    			if(goalLocation != null)
    				clearBox(goalLocation);
    			drawBox(Material.RED_WOOL, commandLocation);
    			goalLocation = commandLocation;
    		} else if(args[0].equals("setSolution")) {
    			Bukkit.broadcastMessage("Ran setSolution.");
    			if(solutionLocation != null)
    				clearBox(solutionLocation);
    			drawBox(Material.GREEN_WOOL, commandLocation);
    			solutionLocation = commandLocation;
    		} else if(args[0].equals("setPiece")) {
    			Bukkit.broadcastMessage("Ran setPiece.");
    			// Sets area as piece pattern for piece number
    			if(args.length < 2) {
    				sender.sendMessage("Usage: blockSolver setPiece <piece number>");
    			}
    			
    			int pieceNumber;
    			try { pieceNumber = Integer.parseInt(args[1]); }
    			catch(NumberFormatException nfe) {
    				sender.sendMessage(ChatColor.RED + "Argument <piece number> must be an integer");
    				return false;
    			}
    			
    			if(pieceNumber < 0 || pieceNumber > 6) {
    				sender.sendMessage(ChatColor.RED + "Argument <piece number> must be between 0 and 6.");
    				return false;
    			}
    			
    			if(pieceLocations.get(pieceNumber) != null)
    				clearBox(pieceLocations.get(pieceNumber));
    			drawBox(Material.ORANGE_WOOL, commandLocation);
    			
    			pieceLocations.set(pieceNumber, commandLocation);
    		} else if(args[0].equals("clear")) {
    			Bukkit.broadcastMessage("Ran clear.");
    			if(goalLocation != null)
    				clearBox(goalLocation);
    			goalLocation = null;
    			if(solutionLocation != null)
    				clearBox(solutionLocation);
    			solutionLocation = null;
    			for(int i = 0; i < pieceLocations.size(); i++) {
    				if(pieceLocations.get(i) != null)
    					clearBox(pieceLocations.get(i));
    				pieceLocations.set(i, null);
    			}
    			
    		} else if(args[0].equals("solve")) {
    			Bukkit.broadcastMessage("Ran solve.");
    			if(goalLocation == null || solutionLocation == null) {
    				Bukkit.broadcastMessage(ChatColor.RED + "Goal location and solution location must be defined first!");
    				return false;
    			}
    			
    			final Location goalLoc = new Location(goalLocation.getWorld(), goalLocation.getX(), goalLocation.getY(), goalLocation.getZ());
    			new BukkitRunnable() {
    				@Override
    				public void run() {
						 
						 Process p = null; try { p = Runtime.getRuntime().exec("blockSolver.exe"); }
						 catch (IOException e) {
						 System.out.println("blockSolver.exe could not be opened."); return; }
						 
						 BufferedWriter bw = new BufferedWriter(new
						 OutputStreamWriter(p.getOutputStream())); BufferedReader br = new
						 BufferedReader(new InputStreamReader(p.getInputStream()));
						 
    					
    					// Read goal location layer by layer and pass input to program.
						Bukkit.broadcastMessage("Starting solver.");
    					try {
							bw.write('8');
							bw.write('\n');
							bw.write('8');
							bw.write('\n');
							bw.write('8');
							bw.write('\n');
							bw.flush();
							for(int i = 1; i < CUBE_SIZE + 1; i++) {
								for(int j = 1; j < CUBE_SIZE + 1; j++) {
									String line = "";
									for(int k = 1; k < CUBE_SIZE + 1; k++) {
										Location l = new Location(goalLoc.getWorld(), goalLoc.getX() + j, goalLoc.getY() + i, goalLoc.getZ() + k);
										if(l.getBlock().getType().equals(Material.AIR))
											line += '.';
										else
											line += 'd';
									}
									bw.write(line);
									bw.write('\n');
									bw.flush();
									//while()
								}
							}
							
							String line;
							br.readLine(); // We already know the cube size
							int numSols = Integer.parseInt(br.readLine());
							solutions = new ArrayList<Solution>();
					        for (int t = 0; t < numSols; t++)
					        {
					            Solution sol;
					            ArrayList<ArrayList<ArrayList<Character>>> inputCube = new ArrayList<ArrayList<ArrayList<Character>>>();
					            for (int i = 0; i < CUBE_SIZE; i++)
					            {
					                inputCube.add(new ArrayList<ArrayList<Character>>());
					                for (int j = 0; j < CUBE_SIZE; j++)
					                {
					                    inputCube.get(i).add(new ArrayList<Character>());
					                    for (int k = 0; k < CUBE_SIZE; k++)
					                    {
					                        inputCube.get(i).get(j).add(Solution.emp);
					                    }
					                }
					            }

					            for (int i = 0; i < CUBE_SIZE; i++)
					            {
					                for (int j = 0; j < CUBE_SIZE; j++)
					                {
					                    line = br.readLine();
					                    for (int k = 0; k < CUBE_SIZE; k++)
					                    {
					                        inputCube.get(j).get(k).set(i, line.charAt(k));
					                    }
					                }
					            }
					            sol = new Solution(inputCube);
					            solutions.add(sol);
					        }
							
	    					p.waitFor();
	    					Bukkit.getServer().broadcastMessage("Done. " + solutions.size() + " unique solutions.");
						} catch (Exception e) {
							System.out.println("Error with I/O for blockSolver.exe");
							e.printStackTrace();
						}
    					
    				}
    			}.runTaskAsynchronously(this);
    		} else if(args[0].equals("showSolutions")) {
    			//Bukkit.broadcastMessage("Ran showSolutions.");
    			if(solutions != null && solutions.size() > 0) {
    				solutions.get(0).showSolution(solutionLocation);
    			}
    		} else if(args[0].equals("nextSolution")) {
    			//Bukkit.broadcastMessage("Ran nextSolution.");
    			if(solutions == null || solutions.size() == 0)
    				return false;
    			nextSolutionNumber++;
    			nextSolutionNumber %= solutions.size();
    			solutions.get(nextSolutionNumber).showSolution(solutionLocation);
    		} else if(args[0].equals("prevSolution")) {
    			//Bukkit.broadcastMessage("Ran prevSolution.");
    			if(solutions == null || solutions.size() == 0)
    				return false;
    			nextSolutionNumber--;
    			if(nextSolutionNumber < 0)
    				nextSolutionNumber = solutions.size() - 1;
    			solutions.get(nextSolutionNumber).showSolution(solutionLocation);
    		}
    		
    	}
    	return false;
    }
    
    void clearBox(Location loc) {
    	drawBox(Material.AIR, loc);
    }
    
    void drawBox(Material border, Location loc) {
    	for(int i = 0; i < CUBE_SIZE + 2; i++) {
    		for(int j = 0; j < CUBE_SIZE + 2; j++) {
    			if(i == 0 && j == 0)
    				continue;
    			if(i == 0 || i == CUBE_SIZE + 1 || j == 0 || j == CUBE_SIZE + 1) {
    				Location l = new Location(loc.getWorld(), loc.getX() + i, loc.getY(), loc.getZ() + j);
    				// Create at yOffset=0
    				l.getBlock().setType(border);
    				
    				//Create at yOffset=CUBE_SIZE+1
    				l = l.add(0, CUBE_SIZE+1, 0);
    				l.getBlock().setType(border);
    				
    				l = new Location(loc.getWorld(), loc.getX(), loc.getY() + i, loc.getZ() + j);
    				//xOffset=0
    				l.getBlock().setType(border);
    				
    				//xOffset=CUBE_SIZE+1
    				l = l.add(CUBE_SIZE+1, 0, 0);
    				l.getBlock().setType(border);
    				
    				l = new Location(loc.getWorld(), loc.getX() + j, loc.getY() + i, loc.getZ());
    				//zOffset=0
    				l.getBlock().setType(border);
    				
    				//zOffset=CUBE_SIZE+1
    				l = l.add(0, 0, CUBE_SIZE+1);
    				l.getBlock().setType(border);
    			}
    		}
    	}
    }
}
