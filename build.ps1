#タグ名称を設定する。
$tags = "5.0.1-001"

$WorkDir="build"

#tagを作成する。
git tag -a $tags -m 'build tag'

#作業ディレクトリを作成する。
Remove-Item -path $WorkDir -recurse
mkdir $WorkDir

#tagからファイルをエクスポートする。
git archive --format zip -o $WorkDir\export.zip $tags

#エクスポートしたファイルを展開する。
cd $WorkDir
$file=Convert-Path(".\export.zip")
$shell = New-Object -ComObject shell.application
$zip = $shell.NameSpace($file)
$dest =  $shell.NameSpace((Split-Path $file -Parent))

$dest.CopyHere($zip.Items()) 

# ビルドを実行する。
cd ENdoSnipe
.\build.bat