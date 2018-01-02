package tutuka;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tutuka.compareTransactions.CSVToBeanReader;
import tutuka.compareTransactions.FuzzyLogicService;
import tutuka.compareTransactions.Transaction;
import tutuka.compareTransactions.TransactionReportWithScore;
import tutuka.storage.StorageService;
import tutuka.utils.EmptyFileException;
import tutuka.utils.FileExtensionException;
import tutuka.utils.FileValidationsUtil;
import tutuka.utils.InvalidHeaderException;
import tutuka.utils.RemoveBadAndDuplicatesUtil;

@Controller
@RequestMapping("app")
public class FileUploadCompareController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StorageService storageService;

    @Autowired
    public FileUploadCompareController(StorageService storageService) {
        this.storageService = storageService;
    }
    
    @Autowired
	FuzzyLogicService fLogic;

    @GetMapping("/upload")
    public ModelAndView listUploadedFiles(Model model) throws IOException {
    	logger.info("Inside upload Get Method");
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadCompareController.class,
                        "serveFile", path.getFileName().toString().substring(path.getFileName().toString().lastIndexOf("\\")+1)).build().toString())
                .collect(Collectors.toList()));
	    ModelAndView mav = new ModelAndView();
	    mav.setViewName("uploadForm");
	    mav.addObject("fileLoader", new FileLoader());
	    return mav;
    }
    
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
    @PostMapping("/upload")
    public ModelAndView UploadAndCompare(@ModelAttribute("fileLoader") FileLoader fileLoader, Model model,
			RedirectAttributes redirectAttributes) {
    	logger.info("Inside Post Method");
    	long startTime1 = System.currentTimeMillis();
    	ModelAndView modelAndView = new ModelAndView();
    	List<TransactionReportWithScore> report = new ArrayList<>();
    	RemoveBadAndDuplicatesUtil util = new RemoveBadAndDuplicatesUtil();
    	
        storageService.store(fileLoader.getFileOne());
        storageService.store(fileLoader.getFileTwo());
        
        //Validates for Empty file, INVALID Headers and INVALID File extension
        FileValidationsUtil fileUtil = new FileValidationsUtil();
        fileUtil.validate(storageService.load(fileLoader.getFileOne().getOriginalFilename()).toString());
        fileUtil.validate(storageService.load(fileLoader.getFileTwo().getOriginalFilename()).toString());
        
        //Read the files into Transaction List
        CSVToBeanReader csvBean = new CSVToBeanReader();
		List<Transaction> tutukaList = csvBean.csvRead(storageService.load(fileLoader.getFileOne().getOriginalFilename()).toString());
		List<Transaction> clientList = csvBean.csvRead(storageService.load(fileLoader.getFileTwo().getOriginalFilename()).toString());
		
		//Remove the bad and duplicate transactions from the transaction List
		util.removeBadAndDuplicatesFileOne(tutukaList, report, fileLoader.getFileOne().getOriginalFilename());
		util.removeBadAndDuplicatesFileTwo(clientList, report, fileLoader.getFileTwo().getOriginalFilename());
		
		//Fuzzy Logic cross matching 		
		report.addAll(fLogic.fuzzyLogicMatch(tutukaList, clientList, fileLoader));
		logger.info("TotalTime = :" + (-startTime1 + (System.currentTimeMillis())));
		
		modelAndView.addObject("report", report);
		modelAndView.setViewName("detailedReportPage");
		
		logger.info("Redirecting to report from post method");
		
		return modelAndView;
    }
    
    @ExceptionHandler({EmptyFileException.class, FileExtensionException.class, DateTimeParseException.class, InvalidHeaderException.class})
    public ModelAndView handleEmptyFileError(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errors", ex.getMessage());
        mav.setViewName("error");
        return mav;
    }
    
    @ExceptionHandler({Exception.class})
    public ModelAndView handleStorageError(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errors", ex.getMessage());
        mav.setViewName("error");
        return mav;
    }
    
}
