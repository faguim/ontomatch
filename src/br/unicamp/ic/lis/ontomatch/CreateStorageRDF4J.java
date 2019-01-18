package br.unicamp.ic.lis.ontomatch;

import java.io.File;
import java.io.IOException;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

public class CreateStorageRDF4J {
	public static void createDB() throws RepositoryException, IOException {
		File dataStore = new File("resources/ontologies/mesh/store");
		
		// É preciso executar o seguinte comando no shell para criar os chunks a partir do .nt gigante
		// split -l 100000 mesh.nt 
		File chunksDir = new File("resources/ontologies/mesh/chunks");

		Repository nativeRep = new SailRepository(new NativeStore(dataStore));
		nativeRep.initialize();
		
		File[] files = chunksDir.listFiles();
		try(RepositoryConnection conn = nativeRep.getConnection()) {
			// create a parser config with preferred settings
			ParserConfig config = new ParserConfig();
			config.set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
			config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);

			// set the parser configuration for our connection
			conn.setParserConfig(config);

			for (File file: files) {
				String fileName = file.getAbsolutePath();
				System.out.println("path/" + fileName);
				try {
					conn.add(file, "file://" + fileName, RDFFormat.NTRIPLES);
				} catch (RDFParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
