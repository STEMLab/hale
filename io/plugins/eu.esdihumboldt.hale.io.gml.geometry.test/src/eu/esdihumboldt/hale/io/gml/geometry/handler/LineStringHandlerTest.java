/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading line string geometries
 * 
 * @author Patrick Lieb
 */
public class LineStringHandlerTest extends AbstractHandlerTest {

	private LineString reference;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863),
				new Coordinate(-39882.89, 273153.86) };
		reference = geomFactory.createLineString(coordinates);
	}

	/**
	 * Test linestring geometries read from a GML 2 file
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml2() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/geom-gml2.xsd").toURI(), 
				getClass().getResource("/data/sample-linestring-gml2.xml").toURI());
		
		// three instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 3. LineStringProperty with LineString defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
		} finally {
			it.close();
		}
	}
	
	/**
	 * Test linestring geometries read from a GML 3 file
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
		getClass().getResource("/data/geom-gml3.xsd").toURI(), 
		getClass().getResource("/data/sample-linestring-gml3.xml").toURI());
		
		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 3. GeometryProperty with LineString defined through coord
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkGeometryPropertyInstance(instance);
			
			// 4. LineStringProperty with LineString defined through pos
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
						
			// 5. LineStringProperty with LineString defined through pointRep
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}
	
	/**
	 * Test linestring geometries read from a GML 3.1 file
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
		getClass().getResource("/data/geom-gml31.xsd").toURI(), 
		getClass().getResource("/data/sample-linestring-gml31.xml").toURI());
		
		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 2. LineStringProperty with LineString defined through coord
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 3. LineStringProperty with LineString defined through pos
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 4. LineStringProperty with LineString defined through pointRep
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
						
			// 5. LineStringProperty with LineString defined through pointProperty
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 6. LineStringProperty with LineString defined through posList
			assertTrue("Sixth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}
	
	/**
	 * Test linestring geometries read from a GML 3.2 file
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLineStringGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
		getClass().getResource("/data/geom-gml32.xsd").toURI(), 
		getClass().getResource("/data/sample-linestring-gml32.xml").toURI());
		
		// five instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. LineStringProperty with LineString defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 2. LineStringProperty with LineString defined through pos
			assertTrue("Second sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 3. LineStringProperty with LineString defined through pointRep
			assertTrue("Third sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
			
			// 4. LineStringProperty with LineString defined through pointProperty
			assertTrue("Fourth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
						
			// 5. LineStringProperty with LineString defined through posList
			assertTrue("Fifth sample feature missing", it.hasNext());
			instance = it.next();
			checkLineStringPropertyInstance(instance);
		} finally {
			it.close();
		}
	}
	
	private void checkGeometryPropertyInstance(Instance instance) {
		Collection<Object> geomVals = PropertyResolver.getValues(instance, "LineString", false);
		assertNotNull(geomVals);
		assertEquals(1, geomVals.size());
		
		Object geom = geomVals.iterator().next();
		assertTrue(geom instanceof Instance);
		
		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance);
	}
	
	private void checkLineStringPropertyInstance(Instance instance) {
		Object[] geomVals = instance.getProperty(
				new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);
		
		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);
		
		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance);
	}
	
	private void checkGeomInstance(Instance geomInstance) {
		assertTrue(geomInstance.getValue() instanceof GeometryProperty<?>);
		
		@SuppressWarnings("unchecked")
		LineString linestring = ((GeometryProperty<LineString>) geomInstance.getValue()).getGeometry();
		assertTrue("Read geometry does not match the reference geometry", 
				linestring.equalsExact(reference));
	}
}