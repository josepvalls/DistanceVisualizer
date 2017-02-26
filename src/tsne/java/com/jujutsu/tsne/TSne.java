package tsne.java.com.jujutsu.tsne;

/**
*
* Author: Leif Jonsson (leif.jonsson@gmail.com)
* 
* This is a Java implementation of van der Maaten and Hintons t-sne 
* dimensionality reduction technique that is particularly well suited 
* for the visualization of high-dimensional datasets
*
*/
public interface TSne {

	double [][] tsne(double[][] X, int k, int initial_dims, double perplexity);
	double [][] tsne(double[][] X, int k, int initial_dims, double perplexity, int maxIterations);

	double [][] tsne(double[][] X, int no_dims, int initial_dims, double perplexity, int max_iter, boolean use_pca);

	static class R {
		public double [][] P;
		public double [] beta;
		public double H;
	}
}
