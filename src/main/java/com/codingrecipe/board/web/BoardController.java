package com.codingrecipe.board.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codingrecipe.board.service.BoardService;
import com.codingrecipe.board.service.CommentService;
import com.codingrecipe.board.vo.BoardDTO;
import com.codingrecipe.board.vo.CommentDTO;
import com.codingrecipe.board.vo.PageDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private BoardService boardService;
	private final CommentService commentService;

	// Constructor for initializing final fields
	@Autowired
	public BoardController(BoardService boardService, CommentService commentService) {
		this.boardService = boardService;
		this.commentService = commentService;
	}

	@GetMapping("/save")
	public String saveForm() {
		return "save";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute BoardDTO boardDTO) {
		int saveResult = boardService.save(boardDTO);
		if (saveResult > 0) {
			return "redirect:/board/paging";
		} else {
			return "save";
		}
	}

	@GetMapping("/fetchBoardList")
	@ResponseBody
	public List<BoardDTO> fetchBoardList() {
		return boardService.findAll();
	}

	@GetMapping("/")
	public String findAll(Model model) {
		List<BoardDTO> boardDTOList = boardService.findAll();
		model.addAttribute("boardList", boardDTOList);
		return "list";
	}

	@GetMapping
	public String findById(@RequestParam("id") Long id,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page, Model model) {
		boardService.updateHits(id);
		BoardDTO boardDTO = boardService.findById(id);
		model.addAttribute("board", boardDTO);
		model.addAttribute("page", page);
		List<CommentDTO> commentDTOList = commentService.findAll(id);
		model.addAttribute("commentList", commentDTOList);
		return "detail";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("id") Long id) {
		boardService.delete(id);
		return "redirect:/";
	}

	@GetMapping("/update")
	public String updateForm(@RequestParam("id") Long id, Model model) {
		BoardDTO boardDTO = boardService.findById(id);
		model.addAttribute("board", boardDTO);
		return "update";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
		boardService.update(boardDTO);
		BoardDTO dto = boardService.findById(boardDTO.getId());
		model.addAttribute("board", dto);
		return "detail";
//        return "redirect:/board?id="+boardDTO.getId();
	}

	// /board/paging?page=2
	@GetMapping("/paging")
	public String paging(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		System.out.println("page = " + page);
		List<BoardDTO> pagingList = boardService.pagingList(page);
		System.out.println("pagingList = " + pagingList);
		PageDTO pageDTO = boardService.pagingParam(page);
		model.addAttribute("boardList", pagingList);
		model.addAttribute("paging", pageDTO);
		return "paging";
	}

}