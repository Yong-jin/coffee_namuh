<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="/css/news.css" rel="stylesheet" type="text/css">
</head>
<body>
	<%@ include file="/WEB-INF/views/header/header.jsp"%>
	<div class="cont_wrap bbs_wrap">
		<div class="cont">
			<div class="cont_box cont_head">
				<div class="cont_boxs">
					<div class="cont_title_wrap">
						<div class="cont_title_info">Namuh NEWS</div>
						<div class="cont_title robo color">
							<h2>NAMUH NOTICE</h2>
						</div>
					</div>
					<div class="cont_text_wrap">
						<div class="cont_text cont_list_map">
							<ul>
								<li>홈</li>
								<li>나무소식</li>
								<li>공지사항</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div class="cont_box brand01">
				<div class="cont_boxs text_center">
					<div class="cont_title_wrap">
						<div class="cont_title">
							<b>나무NMC커피 공지사항 </b>
						</div>
					</div>
					<div class="cont_text_wrap">
						<div class="cont_text">나무NMC커피 소식을 알려드립니다.</div>
					</div>
					<div class="cont_text_wrap">
						<div class="cont_list cont_list_content cont_list_content_search">
							<ul>
								<li>
									<div class="board_search_wrap">
										<div class="board_search">
											<form>
												<input type="hidden" name="bbs_category" id="bbs_category"
													value="1"> <input type="hidden"
													name="bbs_detail_category" id="bbs_detail_category"
													value="">
												<ul>
													<li class="board_search_select">
														<div class="input_select_wrap input_wrap2">
															<select name="bbs_search_category"
																id="bbs_search_category">
																<option value="bbs_tbl_title">제목</option>
																<option value="bbs_tbl_content">내용</option>
																<option value="bbs_tbl_name">작성자</option>
															</select>
														</div>
													</li>
													<li>
														<div class="input_text_wrap input_wrap2">
															<label> <input type="text" name="bbs_search"
																id="bbs_search" class="" value=""
																placeholder="검색어를 입력하세요" required="required">
															</label>
														</div>
													</li>
													<li class="board_search_btn">
														<div class="input_btn_wrap input_wrap2">
															<input type="submit" value="검색" class="btn btn1" id="">
														</div>
													</li>
												</ul>
											</form>
										</div>
									</div>
								</li>
								<li style="text-align: right"></li>
							</ul>
						</div>
					</div>
					<div class="cont_text_wrap">
						<div class="board_list">
							<table>
								<colgroup>
									<col class="board_w75 board_pc">
									<col>
									<col class="board_w150 board_pc">
									<col class="board_w100">
									<col class="board_w100 board_pc">
								</colgroup>
								<thead>
									<tr>
										<th class="board_pc">번호</th>
										<th>제목</th>
										<th class="board_pc">글쓴이</th>
										<th>날짜</th>
										<th class="board_pc">조회</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="board_pc">공지</td>
										<td>
											<div class="text_wrap">
												<div class="text">
													<a href="detail/?bbs_idx=194&bbs_category=1&bbs_page=1">
														<em> 나무NMC커피 원두 관련 공지 </em>
													</a>
												</div>
											</div>
										</td>
										<td class="board_pc">나무NMC커피</td>
										<td>2023-02-21</td>
										<td class="board_pc">815</td>
									</tr>
									<tr>
										<td class="board_pc">2</td>
										<td>
											<div class="text_wrap">
												<div class="text">
													<a
														href="detail/?bbs_idx=199&bbs_category=1&bbs_detail_category=&bbs_page=1">
														<em> 탄소중립실천포인트제 도입 안내 </em>
													</a>
												</div>
											</div>
										</td>
										<td class="board_pc">메가MGC커피</td>
										<td>2023-03-16</td>
										<td class="board_pc">1532</td>
									</tr>
									<tr>
										<td class="board_pc">1</td>
										<td>
											<div class="text_wrap">
												<div class="text">
													<a
														href="detail/?bbs_idx=198&bbs_category=1&bbs_detail_category=&bbs_page=1">
														<em> 메가MGC커피 영양성분표 </em>
													</a>
												</div>
											</div>
										</td>
										<td class="board_pc">메가MGC커피</td>
										<td>2023-03-14</td>
										<td class="board_pc">3028</td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="board_page_wrap">
							<div class="board_page">
								<ul>
									<li class='fo_re'><a><span>처음</span></a></li>
									<li class='board_page_check'><a><span>1</span></a></li>
									<li><a><span>마지막</span></a></li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%@ include file="/WEB-INF/views/footer/footer.jsp"%>
</body>
</html>