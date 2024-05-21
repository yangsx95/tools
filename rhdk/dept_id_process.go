package main

import (
	"bytes"
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"text/template"
)

func createDB(schema string) *sql.DB {
	db, err := sql.Open("mysql", "root:rhzl@2014@tcp(10.6.6.73:3306)/"+schema)
	if err != nil {
		panic(err)
	}
	//db.SetConnMaxLifetime(time.Minute * 3)
	//db.SetMaxOpenConns(10)
	//db.SetMaxIdleConns(10)
	return db
}

type tableRow struct {
	Id         string
	TableName  string
	FieldName  string
	FieldValue string
}

func processTable(db *sql.DB, tableName string, fieldName string) []tableRow {
	sqlStr := generateQueryTargetRowsSql(tableName, fieldName)
	fmt.Printf("开始处理表%s\n", tableName)
	fmt.Printf("查询目标数据：%s\n", sqlStr)

	rows, err := db.Query(sqlStr)
	if err != nil {
		panic(err)
	}

	var targets []tableRow

	for rows.Next() {
		var target tableRow
		target.TableName = tableName
		target.FieldName = fieldName
		if err := rows.Scan(&target.Id, &target.FieldValue); err != nil {
			log.Fatal(err)
		}
		targets = append(targets, target)
	}

	if db.Close() != nil {
		return nil
	}

	return targets

}

func generateQueryTargetRowsSql(tableName string, fieldName string) string {
	sqlTplStr := `select id, {{.fieldName}} from {{.tableName}} where is_deleted = 0`
	sqlTpl, err := template.New("sql").Parse(sqlTplStr)
	if err != nil {
		panic(err)
	}

	var buf bytes.Buffer

	err = sqlTpl.Execute(&buf, map[string]interface{}{
		"tableName": tableName,
		"fieldName": fieldName,
	})
	if err != nil {
		panic(err)
	}
	return buf.String()
}

func main() {
	processTable(createDB("archive_manage"), "archives_ask", "ask_department_id")
}
