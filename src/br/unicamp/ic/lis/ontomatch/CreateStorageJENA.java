package br.unicamp.ic.lis.ontomatch;

import java.io.File;

import org.codehaus.jettison.json.JSONException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

public class CreateStorageJENA {
	public static void main(String[] args) throws JSONException {
		// Depois de executar este trecho é preciso mover a pasta "resources/ontologies/mesh/jena" para: "src/resources/ontologies/mesh"
		String tdbDirectoryPath = "resources/ontologies/mesh/jena"; 
		
		Model model = TDBFactory.createModel(tdbDirectoryPath);
		
		String chucksDirPath = "resources/ontologies/mesh/chunks";
		
		File chucksDir = new File(chucksDirPath);
		for (final File fileEntry : chucksDir.listFiles()) {
            System.out.println(fileEntry.getPath());
            FileManager.get().readModel(model, fileEntry.getPath(), "N-TRIPLES");
	    }
		
		System.out.println("finished");
	}
}
