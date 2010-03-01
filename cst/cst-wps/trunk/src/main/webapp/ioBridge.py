"""Humboldt IOBridge process"""

from pywps.Process import WPSProcess
from eu.esdihumboldt.cst.iobridge.impl import DefaultCstServiceBridge
import types
import os

def createIOBridgeProcess():

    class IOBridgeProcess(WPSProcess):
        def __init__(self):
            WPSProcess.__init__(self, identifier="iobridge",
                    title = "Humboldt CST IOBridge",
                    abstract = """IOBridge process for the Humbold CST.
                    Process acceptes schema file, oml file and input gml
                    file and provides the transformation""")

            self.schema = self.addLiteralInput(identifier="schema",
                            title="Schema file",
                            type = types.StringType,
                            allowedValues = getXSDs())
            self.oml = self.addComplexInput(identifier="oml",
                            title="Ontology mapping file",
                            formats = [{"mimeType":"text/xml"}])
            self.gmlin = self.addComplexInput(identifier="gml",
                            title="Input GML file",
                            formats = [{"mimeType":"text/xml"}])
            self.gmlout = self.addComplexOutput(identifier="gml",
                            title="Output GML file",
                            formats = [{"mimeType":"text/xml"}])

        def execute(self):
            dcsb = DefaultCstServiceBridge()
            transformedGML = dcsb.transform(self.schema.getValue(),
                                            self.oml.getValue(),
                                            self.gmlin.getValue())

            self.gmlout.setValue(transformedGML)

    return IOBridgeProcess()

def getXSDs():
    cstdir = os.path.dirname(os.path.abspath(__file__))
    templates = os.path.join(cstdir, "xsds/")

    xsds = []

    def append(xsds, dir, files):
        if dir.find(".svn") > -1:
            return
        for f in files:
            if f.find(".xsd") > -1:
                f = os.path.join(dir,f)
                f = f.replace(templates,"")
                xsds.append(f)
                return

    os.path.walk(templates, append, xsds)

    return xsds

if __name__ == "__main__":
    getXSDs()
