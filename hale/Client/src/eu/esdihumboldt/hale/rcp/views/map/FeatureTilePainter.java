/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.views.map.tiles.AbstractTilePainter;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileBackground;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileCache;

/**
 * Painter for Features
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureTilePainter extends AbstractTilePainter implements TileBackground {
	
	private static final Log log = LogFactory.getLog(FeatureTilePainter.class);
	
	/**
	 * The reference data tile cache
	 */
	private TileCache referenceCache;
	
	/**
	 * The transformed data tile cache
	 */
	private TileCache transformedCache;
	
	/**
	 * How the map is split
	 */
	private SplitStyle splitStyle = SplitStyle.SOURCE;
	
	/**
	 * Creates a Feature painter for the given control
	 * 
	 * @param canvas the control
	 */
	public FeatureTilePainter(Control canvas) {
		referenceCache = new TileCache(
				new FeatureTileRenderer(DatasetType.reference), this);
		transformedCache = new TileCache(
				new FeatureTileRenderer(DatasetType.transformed), this);
		
		init(canvas, determineMapArea());
		
		InstanceService instances = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		instances.addListener(new HaleServiceListener() {
			
			@Override
			public void update() {
				updateMap(determineMapArea());
			}
			
		});
		
		StyleService styles = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		styles.addListener(new HaleServiceListener() {
			
			@Override
			public void update() {
				resetTiles();
				refresh();
			}
			
		});
		
		/*SchemaService schemas = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		schemas.addListener(new HaleServiceListener() {
			
			@Override
			public void update() {
				updateMap(determineMapArea());
			}
			
		});*/
		
		referenceCache.addTileListener(this);
		transformedCache.addTileListener(this);
	}

	/**
	 * @return
	 */
	private ReferencedEnvelope determineMapArea() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		if (is != null) {
			FeatureCollection<FeatureType, Feature> features = is.getFeatures(DatasetType.reference);
			if (features != null) {
				return features.getBounds();
			}
		}
		
		return null;
	}

	/**
	 * @see AbstractTilePainter#paintTile(GC, int, int, int, int, int, int, int)
	 */
	@Override
	protected void paintTile(GC gc, int tileX, int tileY, int zoom, int x,
			int y, int tileWidth, int tileHeight) {
		
		boolean drawReference;
		boolean drawTransformed;
		
		Region referenceRegion = null;
		Region transformedRegion = null;
		
		int[] separator = null;
		
		final Rectangle tileRect = new Rectangle(x, y, tileWidth, tileHeight);
		final Rectangle control = getControl().getBounds();
		
		switch (splitStyle) {
		case OVERLAY:
			// draw both
			drawReference = true;
			drawTransformed = true;
			break;
		case HORIZONTAL:
			Rectangle upperHalf = new Rectangle(0, 0, control.width, control.height / 2);
			Rectangle lowerHalf = new Rectangle(0, control.height / 2, control.width, control.height - control.height / 2);
			
			drawReference = tileRect.intersects(upperHalf);
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(upperHalf);
			
			drawTransformed = tileRect.intersects(lowerHalf);
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(lowerHalf);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						0, control.height / 2 - 2,
						control.width, control.height / 2 - 2,
						control.width, control.height / 2 + 2,
						0, control.height / 2 + 2};
			}
			
			break;
		case VERTICAL:
			Rectangle leftHalf = new Rectangle(0, 0, control.width / 2, control.height);
			Rectangle rightHalf = new Rectangle(control.width / 2, 0, control.width - control.width / 2, control.height);
			
			drawReference = tileRect.intersects(leftHalf);
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(leftHalf);
			
			drawTransformed = tileRect.intersects(rightHalf);
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(rightHalf);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						control.width / 2 - 2, 0,
						control.width / 2 - 2, control.height,
						control.width / 2 + 2, control.height,
						control.width / 2 + 2, 0};
			}
			
			break;
		case DIAGONAL_UP:
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(new int[]{0, 0, control.width, 0, 0, control.height});
			drawReference = referenceRegion.intersects(tileRect);
			
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(new int[]{0, control.height, control.width, 0, control.width, control.height});
			drawTransformed = transformedRegion.intersects(tileRect);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						0, control.height,
						0, control.height - 2,
						control.width - 2, 0,
						control.width, 0,
						control.width, 2,
						2, control.height};
			}
			
			break;
		case DIAGONAL_DOWN:
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(new int[]{0, 0, control.width, control.height, 0, control.height});
			drawReference = referenceRegion.intersects(tileRect);
			
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(new int[]{0, 0, control.width, 0, control.width, control.height});
			drawTransformed = transformedRegion.intersects(tileRect);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						0, 0,
						2, 0,
						control.width, control.height - 2,
						control.width, control.height,
						control.width - 2, control.height,
						0, 2};
			}
			
			break;
		case TARGET:
			// only draw transformed
			drawReference = false;
			drawTransformed = true;
			break;
		case SOURCE:
			// fall through
		default:
			// only draw reference
			drawReference = true;
			drawTransformed = false;
			break;
		}
		
		// configure GC
		gc.setAntialias(SWT.ON);
		
		// paint background
		if (drawReference || drawTransformed) {
			drawTileBackground(gc, x, y, tileWidth, tileHeight);
		}
		
		// reference
		if (drawReference) {
			drawTile(gc, referenceCache, referenceRegion, tileX, tileY, zoom, x, y, tileWidth, tileHeight);
		}
		
		// transformed
		if (drawTransformed) {
			drawTile(gc, transformedCache, transformedRegion, tileX, tileY, zoom, x, y, tileWidth, tileHeight);
		}
		
		// separator
		if (separator != null) {
			gc.fillPolygon(separator);
		}
	}

	/**
	 * Draw a tile
	 * 
	 * @param gc the graphics object
	 * @param cache the tile cache
	 * @param region the clipping region (it will be disposed)
	 * @param tileX the tile x ordinate
	 * @param tileY the tile y ordinate
	 * @param zoom the tile zoom level
	 * @param x the tile x position
	 * @param y the tile y position
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 */
	private void drawTile(GC gc, TileCache cache,
			Region region, int tileX, int tileY, int zoom, int x, int y,
			int tileWidth, int tileHeight) {
		ImageData imageData;
		try {
			imageData = cache.getTile(this, zoom, tileX, tileY);
		} catch (Exception e) {
			imageData = null;
			log.error("Error getting the tile image", e);
		}
		
		Image image = null;
		if (imageData != null) {
			image = new Image(gc.getDevice(), imageData);
		}
		
		try {
			Rectangle oldClipping = gc.getClipping();
			if (region != null) {
				gc.setClipping(region);
			}
			
			if (image != null) {
				gc.drawImage(image, x, y);
			}
			else {
				Color bg = gc.getBackground();
				
				gc.setBackground(new Color(gc.getDevice(), 128, 128, 128));
				gc.fillRectangle(x, y, tileWidth, tileHeight);
				
				gc.setBackground(bg);
			}
			
			if (region != null) {
				gc.setClipping(oldClipping);
			}
		} finally {
			if (image != null) {
				image.dispose();
			}
			if (region != null) {
				region.dispose();
				region = null;
			}
		}
	}
	
	/**
	 * @see TileBackground#drawTileBackground(GC, int, int, int, int)
	 */
	@Override
	public void drawTileBackground(GC gc, int x, int y, int tileWidth, int tileHeight) {
		Color bg = gc.getBackground();
		
		gc.setBackground(new Color(gc.getDevice(), 126, 166, 210));
		gc.fillRectangle(x, y, tileWidth, tileHeight);
		
		gc.setBackground(bg);
	}

	/**
	 * @see AbstractTilePainter#resetTiles()
	 */
	@Override
	protected void resetTiles() {
		referenceCache.clear();
		transformedCache.clear();
	}

	/**
	 * Get the split style
	 * 
	 * @return the split style
	 */
	public SplitStyle getSplitStyle() {
		return splitStyle;
	}

	/**
	 * Set the split style
	 * 
	 * @param splitStyle the split style
	 */
	public void setSplitStyle(SplitStyle splitStyle) {
		this.splitStyle = splitStyle;
		
		refresh();
	}

}
