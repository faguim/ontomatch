//package br.unicamp.ic.lis.ontomatch.test;
//
//import org.apache.jena.query.Dataset;
//import org.apache.jena.query.ReadWrite;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ResIterator;
//import org.apache.jena.rdf.model.Resource;
//import org.apache.jena.rdf.model.StmtIterator;
//import org.apache.jena.tdb.TDBFactory;
//
//public class TestTDB {
//	
//		public static void main(String[] args) {
//			// Make a TDB-backed dataset
//			String directory = "resources/ontologies/mesh/chunks" ;
//			Dataset dataset = TDBFactory.createDataset(directory) ;
//			
//			dataset.begin(ReadWrite.READ);
//			// Get model inside the transaction
//			Model model = dataset.getDefaultModel() ;
//			
//			ResIterator iterator = model.listSubjects();
//			System.out.println("aquiiiiiiiiiiii");
//			while (iterator.hasNext()) {
//				Resource resource = iterator.next();
//				System.out.println(resource);
//				
//			}
//			
//			dataset.end() ;
//			
//			
//		}
//}
