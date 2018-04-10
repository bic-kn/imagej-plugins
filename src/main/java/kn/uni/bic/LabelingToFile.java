// TODO Missing license header

package kn.uni.bic;

import java.io.File;
import java.io.IOException;

//import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.labeling.Labeling;
import net.imglib2.roi.Regions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.io.IOService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.text.TextService;

/**
 * Reads a {@link Labeling} from a text file that is located at a specified
 * location and is of the form:
 * <p>
 * <code>width,height,channel,x,y</code>
 * </p>
 * 
 * @author Stefan Helfrich (University of Konstanz)
 */
@SuppressWarnings({ "deprecation", "javadoc" })
@Plugin(type = Command.class, headless = true,
	menuPath = "BIC>Labeling To File")
public class LabelingToFile<T extends RealType<T>> implements Command {

	@Parameter
	private OpService opService;

	@Parameter
	private IOService ioService;

	@Parameter(required = false, label = "Location of file")
	private File filePath;

	@Parameter
	private ImgLabeling<Integer, ?> labeling;

	@Override
	public void run() {
		try {
			// Read line representation from file
			String content = "";
			Interval roiInterval = parse(content);

			// Crop additional dimensions (== creating a 2D plane)
			int[] dimensions = Intervals.dimensionsAsIntArray(img);
			Interval imgInterval = Intervals.createMinMax(0, 0, dimensions[0] - 1,
				dimensions[1] - 1);
			Img<IntType> tempImg = opService.create().img(imgInterval, new IntType());

			// Create ImgLabeling
			out = opService.create().imgLabeling(tempImg);

			// Write label to the pixels within the line
			IntervalView<LabelingType<Integer>> roi = Views.interval(out,
				roiInterval);
			roi.forEach(pixel -> pixel.add(1));
			// LabelRegions<Integer> labelRegions = new LabelRegions<>(out);
		}
		catch (IOException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
	}

	private Interval parse(String content) {
		String[] roiDef = content.trim().split(",");
		int width = (int) Double.parseDouble(roiDef[0]);
		int height = (int) Double.parseDouble(roiDef[1]);
		int x = Integer.parseInt(roiDef[3]);
		int y = Integer.parseInt(roiDef[4]);
		long[] min = new long[] { x, y };
		long[] max = new long[] { x + width, y + height };
		return new FinalInterval(min, max);
	}

	public static void main(String[] args) {
		// // create the ImageJ application context with all available services
		// final ImageJ ij = net.imagej.Main.launch(args);
		//
		// ij.command().run(LabelingFromFile.class, true);
	}
}
