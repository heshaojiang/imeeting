package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.grgbanking.grgacd.common.TerminalStatus;
import com.grgbanking.grgacd.common.TerminalType;
import com.grgbanking.grgacd.model.AcdTerminal;
import com.grgbanking.grgacd.service.AcdTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 终端表，用于保存agent可以登录的授权的终端 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-24
 */
@RestController
@RequestMapping("/terminal")
public class AcdTerminalController {

    @Autowired
    AcdTerminalService terminalService;

    @GetMapping("/page")
    public R<Page> getTerminalList(@RequestParam Map<String, Object> params){
        Page<AcdTerminal> terminalPage = terminalService.getTerminalPage(new Query<>(params));
        return new R<>(Boolean.TRUE,terminalPage);
    }

    @PostMapping
    public R<Boolean> addTerminal(@RequestBody AcdTerminal terminal){
        boolean flag = terminalService.insert(terminal);
        return new R<>(flag);
    }

    @PutMapping
    public R<Boolean> updateTerminal(@RequestBody AcdTerminal terminal){
        terminal.setUpdateTime(new Date());
        boolean flag = terminalService.updateById(terminal);
        return new R<>(flag);
    }

    @DeleteMapping("/{id}")
    public R<Boolean> deleteTerminal(@PathVariable("id") String[] TerIds) {
        boolean flag = terminalService.batchDeleteTerminal(TerIds);
        return new R<>(flag);
    }

    @GetMapping("/{terminalNo}")
    public R<AcdTerminal> getTerminal(@PathVariable  String terminalNo){
        AcdTerminal terminal = terminalService.selectById(terminalNo);
        return new R<>(Boolean.TRUE,terminal);
    }

    @GetMapping("/type")
    public R<Object> getTerminalType(){
        List<String> types=new ArrayList<>();
        for (TerminalType type: TerminalType.values()){
            types.add(type.name());
        }
        return new R<>(Boolean.TRUE,types);
    }

    @GetMapping("/status")
    public R<Object> getTerminalStatus(){
        List<String> status=new ArrayList<>();
        for (TerminalStatus type: TerminalStatus.values()){
            status.add(type.name());
        }
        return new R<>(Boolean.TRUE,status);
    }

    @PutMapping("/statusChange")
    public R<Boolean> changeTerminalStatus(String terminalNo,boolean checked){
        String status;
        if (checked){
            status= TerminalStatus.AVAILABLE.name();
        }else {
            status=TerminalStatus.NOTACTIVE.name();
        }
        boolean flag = terminalService.changeStatus(terminalNo, status);
        return new R<>(flag);
    }

}

