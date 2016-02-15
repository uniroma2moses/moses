function new_nodo=build(nodi,classe,root,sink,curr_nodo)

% FUNCTION build
%
% build is a recursive function which generates the global data structure node. 
% Each node represents either a workflow node (an abstract service or branching/forking/sink node in the workflow)
% or subgraphs correspodning to a fork-join pair in the workflow. 

global node

node(curr_nodo).set=nodi;
node(curr_nodo).tipo=10;
node(curr_nodo).classe=classe;

oldr=classe(1).r;
r=classe(1).r;

r(find(r<0))=0;

% find fork nodes

fork=[];

for i=nodi
    if(node(i).tipo==1 & i~=root)
        fork=[fork i];    
    end
end

% select outer fork nodes 

for i=fork
    if(i~=root);
        conta=0;
        indi=find(nodi==i);
        app=find(r(:,indi));
        j=nodi(app);
        
        if(j~=root & abs(node(j).tipo==1))
            conta=conta+node(j).tipo;
        end
        while(j~=root)
            indj=find(nodi==j);
            app=find(r(:,j));
            app=app(1);
            j=nodi(app);
            if(j~=root & abs(node(j).tipo==1))
                conta=conta+node(j).tipo;
            end
        end    
        if conta~=0
            fork=setdiff(fork,i);    
        end
    end
end


new_nodo=curr_nodo;

for i=fork % for each fork/join find the all the nodes between them
    
    new_nodo=new_nodo+1;
      
    j=i; conta=1;
    while(conta)
        indj=find(nodi==j);
        app=find(r(indj,:));
        
        app=app(1);
        j=nodi(app);
        if(abs(node(j).tipo)==1)
            conta=conta+node(j).tipo;    
        end
    end
    
    fjnodi=[i];
    fjapp=fjnodi;
    while(~isempty(fjapp))
        l=fjapp(1); fjapp(1)=[];
        if(l~=j)
            indl=find(nodi==l);
            fjapp=[fjapp nodi(find(r(indl,:)))];
            fjnodi=[fjnodi nodi(find(r(indl,:)))];
        end
    end
    fjnodi=[fjnodi j];
    
 
    fjnodi=unique(fjnodi);
    
    node(curr_nodo).set=setdiff(node(curr_nodo).set,fjnodi);
    node(curr_nodo).set=[node(curr_nodo).set new_nodo];
    
    
    % for each service class
    for m=1:length(classe);
        
        %  update the graph by inserting the "fork/join nodes"
    
        appset=node(curr_nodo).set;
        
        appset(end)=[];
        appr=node(curr_nodo).classe(m).r;
        
        indappset=[];
        for l=1:length(appset)
            indappset=[indappset find(nodi==appset(l))];      
        end
        indj=find(nodi==j); indi=find(nodi==i);
        
        appr=appr([indappset indj],[indappset indi]);
        
        node(curr_nodo).classe(m).r=appr;
        
        indfjnodi=[];
        for l=1:length(fjnodi)
            indfjnodi=[indfjnodi find(nodi==fjnodi(l))];      
        end    
        
        nclasse(m).r=oldr(indfjnodi,indfjnodi);    
    
    end
   
    
    if(find(indfjnodi==root))
        root=new_nodo;
    elseif(find(indfjnodi==sink))
        sink=new_nodo;    
    end
    
    nodi=node(curr_nodo).set;
    
    new_nodo=build(fjnodi,nclasse,i,j,new_nodo);
    
end    

node(curr_nodo).root=root;
node(curr_nodo).sink=sink;

r=node(curr_nodo).classe(1).r;

r(find(r<0))=0;

inds=find(nodi==root);
pathsr=nodi(find(r(inds,:)));

node(curr_nodo).pathsr=pathsr;

conta=0;
for i=pathsr
    conta=conta+1;
    
    pathsnodi=[i];
    pathsapp=pathsnodi;
    while(~isempty(pathsapp))
        l=pathsapp(1);pathsapp(1)=[];
        if(l~=sink)
            indl=find(nodi==l);
            pathsapp=[pathsapp nodi(find(r(indl,:)))];
            pathsnodi=[pathsnodi nodi(find(r(indl,:)))];
        end
    end
    pathsnodi=unique(pathsnodi); 
    pathsnodi=setdiff(pathsnodi,sink);
    
    % keyboard;
    
    node(curr_nodo).p{conta}=pathsnodi;
    
    indpathsnodi=[];
    for l=1:length(pathsnodi)
        indpathsnodi=[indpathsnodi find(nodi==pathsnodi(l))];      
    end    
    
    for m=1:length(classe)
        node(curr_nodo).classe(m).proute{conta}=node(curr_nodo).classe(m).r(indpathsnodi,indpathsnodi);  
    end
    
end